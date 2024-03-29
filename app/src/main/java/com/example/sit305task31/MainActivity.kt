package com.example.sit305task31

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sit305task31.ui.theme.SIT305TASK31Theme
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizApp()
        }
    }
}
// StartPage composable function displays the start page, allowing the user to enter a name and start the game
@Composable
fun StartPage(onStartClick: (String) -> Unit) {
    val (name, setName) = remember { mutableStateOf("") } // Use remember to save username input status

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Quiz App")
        TextField(
            value = name,
            onValueChange = setName,
            label = { Text("Enter your name") }
        )
        Button(
            onClick = { onStartClick(name) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Start")
        }
    }
}
// QuizApp can compose functions to manage the state and logic of the entire application
@Composable
fun QuizApp() {  // Save user name, current question index, score and other status through remember
    var userName by remember { mutableStateOf("") }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    //Define question list
    val questions = listOf(
        Question("Question 1. 1 + 1 = ?", listOf("Answer: 1", "Answer: 2", "Answer: 3"), "Answer: 2"),
        Question("Question 2. 2 + 1 = ？", listOf("Answer：2", "Answer：1", "Answer：3"), "Answer：3"),
        Question("Question 3. 7 + 4 = ?", listOf("Answer: 11", "Answer: 12", "Answer: 13"), "Answer: 11"),
        Question("Question 4. 5 + 8 = ?", listOf("Answer: 21", "Answer: 22", "Answer: 13"), "Answer: 13"),
        Question("Question 5. 12 + 13 = ?", listOf("Answer: 18", "Answer: 25", "Answer: 31"), "Answer: 25")
    )
    //Display different interfaces based on status
    if (userName.isEmpty()) {
        StartPage(onStartClick = { userName = it })
    } else if (currentQuestionIndex >= questions.size) {
        ResultScreen(score, questions.size) {
            userName = "" // Reset userName to allow restart
            currentQuestionIndex = 0
            score = 0
            selectedAnswer = ""
        }
    } else {
        QuizScreen(
            question = questions[currentQuestionIndex],
            userName = userName,
            onUserNameChange = { userName = it },
            onAnswerSelected = { selectedAnswer = it },
            onSubmit = {
                if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                    score++
                }
                currentQuestionIndex++
            },
            progress = (currentQuestionIndex.toFloat()) / questions.size,
            selectedAnswer = selectedAnswer
        )
    }
}
// Question data class, representing a question and its answer
data class Question(val text: String, val answers: List<String>, val correctAnswer: String)

// QuizScreen composable function to display the current question and answer options
@Composable
fun QuizScreen(
    question: Question,
    userName: String,
    onUserNameChange: (String) -> Unit,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    progress: Float,
    selectedAnswer: String
) {
    var showResult by remember { mutableStateOf(false) } // Use remember to record whether to display the answer result

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = progress)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Welcome, $userName")
        Text(question.text)
        question.answers.forEach { answer ->
            Button(
                onClick = {
                    if (!showResult) {
                        onAnswerSelected(answer)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showResult && selectedAnswer == answer) {
                        if (answer == question.correctAnswer) Color.Green else Color.Red
                    } else Color.LightGray
                )
            ) {
                Text(answer)
            }
        }
        Button(
            onClick = {
                if (!showResult && selectedAnswer.isNotEmpty()) {
                    showResult = true  //Display the results after the user submits the answer
                } else {
                    showResult = false  //Reset the display result state when the user clicks on the next question
                    onSubmit()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showResult) "Next" else "Submit")  //Change the button text based on whether the result is displayed or not
        }
    }
}

// ResultScreen composable function that displays the final score and provides options to restart or end
@Composable
fun ResultScreen(score: Int, total: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Congratulations! Your total score is $score/$total")
        Button(onClick = onRestart) {
            Text("Take new quiz")
        }
        Button(onClick = { System.exit(0) }) {
            Text("Finish")
        }
    }
}