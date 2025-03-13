package com.example.designyourt_shirt.uiScreen.Profile




import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream


// Move the updateUserField function outside of the ProfileScreen composable
fun updateUserField(context: android.content.Context, fieldName: String, value: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        FirebaseDatabase.getInstance().getReference("Users")
            .child(userId)
            .child(fieldName)
            .setValue(value)
            .addOnSuccessListener {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController
) {
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var imageString by remember { mutableStateOf("") }
    val context = LocalContext.current

    // For edit dialogs
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }

    // New field values
    var newUsername by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }

    // For image picking
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            val encodedImage = encodeBitmapToBase64(bitmap)
            updateUserField(context, "imageUrl", encodedImage)
        }
    }

    // Load user data
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        username = snapshot.child("username").getValue(String::class.java) ?: ""
                        email = snapshot.child("email").getValue(String::class.java) ?: ""
                        phone = snapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                        imageString = snapshot.child("imageUrl").getValue(String::class.java) ?: ""

                        // Initialize edit fields with current values
                        newUsername = username
                        newEmail = email
                        newPhone = phone

                        isLoading = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isLoading = false
                        Toast.makeText(context, "Error loading profile", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // Function to update user data in Firebase
    fun updateUserField(fieldName: String, value: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child(fieldName)
                .setValue(value)
                .addOnSuccessListener {
                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                }
        }
    }



    // Logout function
    fun logout() {
        val navController = navController
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navController.navigate("SignIn")
    }

    // Username edit dialog
    if (showUsernameDialog) {
        AlertDialog(
            onDismissRequest = { showUsernameDialog = false },
            title = { Text("Edit Username") },
            text = {
                TextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Username") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateUserField("username", newUsername)
                        showUsernameDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showUsernameDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Email edit dialog
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = { Text("Edit Email") },
            text = {
                TextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Email") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateUserField("email", newEmail)
                        showEmailDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEmailDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Phone edit dialog
    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text("Edit Phone") },
            text = {
                TextField(
                    value = newPhone,
                    onValueChange = { newPhone = it },
                    label = { Text("Phone") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateUserField("phoneNumber", newPhone)
                        showPhoneDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showPhoneDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            error != null -> {
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Image with Edit Button
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .padding(8.dp)
                    ) {
                        if (imageString.isNotEmpty()) {
                            val bitmap = decodeBase64ToImage(imageString)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Image edit button
                        FloatingActionButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Picture",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    // User Information Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Username Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Username",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = username,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                IconButton(onClick = {
                                    newUsername = username
                                    showUsernameDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Username",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Email Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Email",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = email,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                IconButton(onClick = {
                                    newEmail = email
                                    showEmailDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Email",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Phone Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Phone",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                IconButton(onClick = {
                                    newPhone = phone
                                    showPhoneDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Phone",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

//                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Button
                    Button(
                        onClick = { logout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Logout")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                           navController.navigate("Upload")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Text(
                            text = "Upload designs for T-Shirt",
                            color = Color.White,

                        )
                    }


                }
            }
        }
    }
}




fun decodeBase64ToImage(base64Str: String): Bitmap {
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun encodeBitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}