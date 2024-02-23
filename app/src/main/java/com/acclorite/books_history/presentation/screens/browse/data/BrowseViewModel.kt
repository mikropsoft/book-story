package com.acclorite.books_history.presentation.screens.browse.data

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acclorite.books_history.domain.model.NullableBook
import com.acclorite.books_history.domain.use_case.FastGetBooks
import com.acclorite.books_history.domain.use_case.GetBooksFromFiles
import com.acclorite.books_history.domain.use_case.GetFilesFromDownloads
import com.acclorite.books_history.domain.use_case.InsertBooks
import com.acclorite.books_history.presentation.Argument
import com.acclorite.books_history.presentation.Screen
import com.acclorite.books_history.util.Resource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPermissionsApi::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getBooksFromFiles: GetBooksFromFiles,
    private val getFilesFromDownloads: GetFilesFromDownloads,
    private val insertBooks: InsertBooks,
    private val fastGetBooks: FastGetBooks
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    init {
        onEvent(BrowseEvent.OnLoadList)
    }

    fun onEvent(event: BrowseEvent) {
        when (event) {
            is BrowseEvent.OnLegacyStoragePermissionRequest -> {
                event.permissionState.launchPermissionRequest()

                viewModelScope.launch {
                    for (i in 0 until 100) {
                        if (!event.permissionState.status.isGranted) {
                            delay(100)
                            continue
                        }
                        _state.update {
                            it.copy(
                                requestPermissionDialog = false,
                                showErrorMessage = false
                            )
                        }
                        onEvent(BrowseEvent.OnRefreshList)
                        break
                    }
                }
            }

            is BrowseEvent.OnStoragePermissionRequest -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    val uri = Uri.parse("package:${event.activity.packageName}")
                    val intent = Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                    event.activity.startActivity(intent)

                    viewModelScope.launch {
                        for (i in 0 until 20) {
                            if (!Environment.isExternalStorageManager()) {
                                delay(1000)
                                continue
                            }
                            _state.update {
                                it.copy(
                                    requestPermissionDialog = false,
                                    showErrorMessage = false
                                )
                            }
                            onEvent(BrowseEvent.OnRefreshList)
                            break
                        }
                    }
                }
            }

            is BrowseEvent.OnStoragePermissionDismiss -> {
                val legacyPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                val isPermissionGranted =
                    if (!legacyPermission) Environment.isExternalStorageManager()
                    else event.permissionState.status.isGranted

                _state.update {
                    it.copy(
                        requestPermissionDialog = false,
                        showErrorMessage = !isPermissionGranted
                    )
                }

                if (isPermissionGranted) {
                    viewModelScope.launch(Dispatchers.IO) {
                        getFilesFromDownloads()
                    }
                }

            }

            is BrowseEvent.OnRefreshList -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(
                            isRefreshing = true,
                            selectedItemsCount = 0,
                            hasSelectedItems = false,
                            showSearch = false,
                            selectableFiles = emptyList()
                        )
                    }

                    getFilesFromDownloads("")
                    delay(1000)
                    _state.update {
                        it.copy(
                            isRefreshing = false
                        )
                    }
                }
            }

            is BrowseEvent.OnPermissionCheck -> {
                val legacyPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                val isPermissionGranted =
                    if (!legacyPermission) Environment.isExternalStorageManager()
                    else event.permissionState.status.isGranted

                if (isPermissionGranted) {
                    return
                }
                _state.update {
                    it.copy(
                        requestPermissionDialog = true,
                        showErrorMessage = false
                    )
                }
            }

            is BrowseEvent.OnSelectFile -> {
                val indexOfFile = _state.value.selectableFiles.indexOf(event.file)
                val editedList = _state.value.selectableFiles.toMutableList()
                editedList[indexOfFile] = editedList[indexOfFile].copy(
                    second = !editedList[indexOfFile].second
                )

                _state.update {
                    it.copy(
                        selectableFiles = editedList.toList(),
                        selectedItemsCount = editedList.filter { file -> file.second }.size,
                        hasSelectedItems = editedList.any { file -> file.second }
                    )
                }
            }

            is BrowseEvent.OnSelectBook -> {
                val indexOfFile = _state.value.selectedBooks.indexOf(event.book)
                val editedList = _state.value.selectedBooks.toMutableList()
                editedList[indexOfFile] = NullableBook.NotNull(
                    editedList[indexOfFile].book!!.copy(
                        second = !editedList[indexOfFile].book!!.second
                    )
                )

                if (!editedList.any { it.book?.second == true }) {
                    return
                }

                _state.update {
                    it.copy(
                        selectedBooks = editedList
                    )
                }
            }

            is BrowseEvent.OnSearchShowHide -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val shouldHide = _state.value.showSearch

                    if (shouldHide) {
                        getFilesFromDownloads("")
                    } else {
                        _state.update {
                            it.copy(
                                searchQuery = "",
                                hasFocused = false
                            )
                        }
                    }
                    _state.update {
                        it.copy(
                            showSearch = !shouldHide
                        )
                    }
                }
            }

            is BrowseEvent.OnRequestFocus -> {
                if (!_state.value.hasFocused) {
                    event.focusRequester.requestFocus()
                    _state.update {
                        it.copy(
                            hasFocused = true
                        )
                    }
                }
            }

            is BrowseEvent.OnClearSelectedFiles -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(
                            selectableFiles = it.selectableFiles.map { file -> file.copy(second = false) },
                            hasSelectedItems = false
                        )
                    }
                }
            }

            is BrowseEvent.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = event.query
                    )
                }
                job?.cancel()
                job = viewModelScope.launch {
                    delay(500)
                    getFilesFromDownloads()
                }
            }

            is BrowseEvent.OnAddingDialogDismiss -> {
                _state.update {
                    it.copy(
                        showAddingDialog = false
                    )
                }
            }

            is BrowseEvent.OnAddingDialogRequest -> {
                _state.update {
                    it.copy(
                        showAddingDialog = true,
                        selectedBooks = emptyList()
                    )
                }
                onEvent(BrowseEvent.OnGetBooksFromFiles)
            }

            is BrowseEvent.OnGetBooksFromFiles -> {
                viewModelScope.launch(Dispatchers.IO) {
                    getBooksFromFiles.execute(
                        _state.value.selectableFiles
                            .filter { it.second }
                            .map { it.first }
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                if (result.data?.isEmpty() ?: return@collect) {
                                    return@collect
                                }
                                _state.update {
                                    it.copy(
                                        selectedBooks = result.data,
                                        isBooksLoading = false
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                _state.update {
                                    it.copy(
                                        isBooksLoading = result.isLoading
                                    )
                                }
                            }

                            is Resource.Error -> Unit
                        }
                    }
                }
            }

            is BrowseEvent.OnAddBooks -> {
                viewModelScope.launch {
                    val booksToInsert = _state.value.selectedBooks
                        .filterIsInstance<NullableBook.NotNull>()
                        .filter { it.book!!.second }
                        .map { it.book!!.first }

                    if (booksToInsert.isEmpty()) {
                        return@launch
                    }

                    insertBooks.execute(booksToInsert)
                    val books = fastGetBooks.execute("")

                    event.resetScroll()
                    _state.update {
                        it.copy(
                            showAddingDialog = false
                        )
                    }
                    onEvent(BrowseEvent.OnClearSelectedFiles)

                    event.navigator.navigate(Screen.LIBRARY, Argument("added_books", books))
                }
            }

            is BrowseEvent.OnLoadList -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                    getFilesFromDownloads("")
                }
            }

            is BrowseEvent.OnUpdateScrollIndex -> {
                _state.update {
                    it.copy(
                        scrollIndex = event.index
                    )
                }
            }

            is BrowseEvent.OnUpdateScrollOffset -> {
                _state.update {
                    it.copy(
                        scrollOffset = event.offset
                    )
                }
            }
        }
    }

    private suspend fun getFilesFromDownloads(query: String = _state.value.searchQuery) {
        getFilesFromDownloads.execute(query).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            selectableFiles = result.data?.map { book -> Pair(book, false) }
                                ?: emptyList(),
                            isLoading = false
                        )
                    }
                }

                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            isLoading = result.isLoading
                        )
                    }
                }

                is Resource.Error -> Unit
            }
        }
    }
}