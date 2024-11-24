package com.example.chatter.feature.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatter.R

@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.size(250.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Image"
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Full Name") },
                label = { Text(text = "Full Name") })
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Email") },
                label = { Text(text = "Email") })

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Password") },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Confirm Password") },
                label = { Text(text = "Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                        password != confirmPassword
            )

            Spacer(modifier = Modifier.size(16.dp))

            Button(modifier = Modifier.fillMaxWidth(),
                enabled =  password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                        password != confirmPassword,
                onClick = { /*TODO*/ }) {
                Text(text = "Sign In")
            }
            TextButton(onClick = { navController.navigate("signin") }) {
                Text(text = "Already have a account? Sign In")
            }
        }
    }
}

@Composable
@Preview
private fun SignUpPreview() {
    SignUpScreen(navController = rememberNavController())
}