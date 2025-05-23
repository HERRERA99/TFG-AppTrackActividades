package com.aitor.trackactividades.core.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun PublicationsList(
    publications: LazyPagingItems<Publication>,
    navigateToActivity: (Long) -> Unit,
    viewModel: PostInteractionViewModel?,
    modifier: Modifier = Modifier,
    navigateToProfile: (Int) -> Unit
) {
    val isRefreshing = publications.loadState.refresh is LoadState.Loading

    SwipeRefresh(
        state = remember { SwipeRefreshState(isRefreshing) },
        onRefresh = { publications.refresh() }
    ) {
        LazyColumn(modifier = modifier) {
            items(publications.itemCount) {
                publications[it]?.let { publication ->
                    PublicationItem(
                        publication = publication,
                        navigateToActivity = navigateToActivity,
                        viewModel = viewModel,
                        navigateToProfile = navigateToProfile
                    )
                }
            }
        }
    }
}