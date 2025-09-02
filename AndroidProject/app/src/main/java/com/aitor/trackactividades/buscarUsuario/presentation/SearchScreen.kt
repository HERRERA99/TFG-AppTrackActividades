package com.aitor.trackactividades.buscarUsuario.presentation

import android.Manifest
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.aitor.trackactividades.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    navigateToProfile: (Int) -> Unit,
    navigateToFeed: () -> Unit,
) {
    val query by searchViewModel.searchQuery.collectAsState()
    val userPagingItems = searchViewModel.users.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text ="Buscar",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ) },
                navigationIcon = {
                    IconButton(onClick = navigateToFeed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { searchViewModel.onSearchQueryChanged(it) },
                label = { Text("Buscar usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(userPagingItems.itemCount) {
                    userPagingItems[it]?.let { user ->
                        Log.d("SearchScreen", "User: $user")
                        UserSearchItem(user = user, onClick = { navigateToProfile(user.id) })

                        Divider(
                            color = MaterialTheme.colorScheme.onBackground,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                userPagingItems.apply {
                    when {
                        loadState.refresh is androidx.paging.LoadState.Loading && query.isNotBlank() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        loadState.append is androidx.paging.LoadState.Loading && query.isNotBlank() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        loadState.refresh is androidx.paging.LoadState.Error && query.isNotBlank() -> {
                            item { Text("Error al cargar resultados") }
                            Log.e(
                                "SearchScreen",
                                "Error al cargar resultados",
                                (loadState.refresh as androidx.paging.LoadState.Error).error
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun UserSearchItem(user: UserSearchModel, isCreator: Boolean = false , onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "@${user.userName}",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (isCreator) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.crown_icon),
                contentDescription = "Icono de Crown",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}