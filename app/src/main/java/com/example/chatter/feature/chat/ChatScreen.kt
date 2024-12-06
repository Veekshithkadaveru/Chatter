package com.example.chatter.feature.chat

import android.Manifest
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chatter.R
import com.example.chatter.model.Message
import com.example.chatter.ui.theme.DarkGrey
import com.example.chatter.ui.theme.Purple
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(navController: NavController, channelId: String, channelName: String) {

    val chooserDialog = remember { mutableStateOf(false) }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    val viewModel: ChatViewModel = hiltViewModel()

    val cameraImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture())
        { success ->
            if (success) {
                cameraImageUri.value?.let {
                    viewModel.sendImageMessage(it, channelId)
                }
            }
        }

    val imageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { viewModel.sendImageMessage(it, channelId) }
        }

    fun createImageUri(): Uri {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir =
            navController.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: throw IllegalStateException("External storage directory is unavailable")
        return FileProvider.getUriForFile(
            navController.context,
            "${navController.context.packageName}.provider",
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                cameraImageUri.value = Uri.fromFile(this)
            }
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraImageLauncher.launch(createImageUri())
            }
        }

    Scaffold(containerColor = Color.Black) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {


            LaunchedEffect(key1 = true) {
                viewModel.listenForMessages(channelId)
            }
            val messages = viewModel.message.collectAsState()

            ChatMessages(messages = messages.value, onSendMessage = { message ->
                viewModel.sendMessage(channelId, message)
            },
                onImageClicked = {
                    chooserDialog.value = true
                })
        }

        if (chooserDialog.value) {
            ContentSelectionDialog(
                onCameraSelected = {
                    chooserDialog.value = false

                    if (navController.context.checkSelfPermission(Manifest.permission.CAMERA)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraImageLauncher.launch(createImageUri())
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    chooserDialog.value = false
                    imageLauncher.launch("image/*")
                })
        }
    }
}

@Composable
fun ContentSelectionDialog(onCameraSelected: () -> Unit, onGallerySelected: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = onCameraSelected) {
                Text(text = "Camera", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onGallerySelected) {
                Text(text = "Gallery", color = Color.Black)
            }
        },
        title = { Text(text = "Select your source") },
        text = { Text(text = "Would you like to pick an image from the gallery or use the") }
    )
}

@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onImageClicked: () -> Unit
) {

    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                ChatBubble(message = message)

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGrey)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                msg.value = ""
                onImageClicked()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.attach),
                    contentDescription = "Attach Files"
                )

            }
            TextField(value = msg.value, onValueChange = { msg.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = "Type a Message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboardController?.hide()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkGrey,
                    unfocusedContainerColor = DarkGrey,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White
                )
            )
            IconButton(onClick = {
                onSendMessage(msg.value)
                msg.value = ""
            }) {
                Image(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Send"
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {

    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val textColor = if (isCurrentUser)
        MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
    val bubbleColor = if (isCurrentUser) Purple else Color.LightGray
    val bubbleShape = if (isCurrentUser) {
        RoundedCornerShape(
            topStart = 26.dp,
            topEnd = 0.dp,
            bottomEnd = 16.dp,
            bottomStart = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 26.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp,
            bottomStart = 0.dp
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        val alignment = if (!isCurrentUser) Alignment.CenterStart else Alignment.CenterEnd

        Row(
            modifier = Modifier
                .padding(8.dp)
                .align(alignment),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!isCurrentUser) {
                Image(
                    painter = painterResource(id = R.drawable.friend),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),

                    )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(color = bubbleColor, shape = bubbleShape)
                    .padding(8.dp)
            ) {
                if (message.imageUrl != null) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = message.message?.trim() ?: "",
                        color = textColor,

                        )
                }
            }
        }
    }
}
