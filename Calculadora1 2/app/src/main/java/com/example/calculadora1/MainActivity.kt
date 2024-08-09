package com.example.calculadora1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.exp
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el display
        display = findViewById(R.id.display)

        // Configurar cada botón
        val buttons = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_AC, R.id.button_sqr,
            R.id.button_exp, R.id.button_divide, R.id.button_x, R.id.button_subs,
            R.id.button_add, R.id.button_parizq, R.id.button_parder, R.id.button_equal
        )

        buttons.forEach { buttonId ->
            findViewById<Button>(buttonId).setOnClickListener { handleButtonClick(it) }
        }
    }

    private fun handleButtonClick(view: android.view.View) {
        when (view.id) {
            R.id.button_equal -> {
                try {
                    val expression = display.text.toString()
                    val result = Calculator.evaluate(expression)
                    display.text = result.toString()
                } catch (e: Exception) {
                    display.text = "Error"
                }
            }
            R.id.button_AC -> display.text = "" // Limpiar la pantalla
            else -> {
                val button = view as Button
                display.text = buildString {
                    append(display.text)
                    append(button.text)
                }
            }
        }
    }
}

object Calculator {

    // Función para verificar la precedencia de los operadores
    private fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            '*', '/' -> 2
            '^', 'r' -> 3
            'e' -> 4
            else -> -1
        }
    }

    // Función para aplicar una operación aritmética
    private fun applyOp(a: Double, b: Double, op: Char): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> a / b
            '^' -> a.pow(b)
            'r' -> b.pow(1 / a) // raíz a-ésima de b
            'e' -> exp(b) // función exponencial e^b, se ignora a
            else -> throw UnsupportedOperationException("Operador no soportado")
        }
    }

    // Función para convertir una expresión infija a postfija
    fun infixToPostfix(expression: String): String {
        val result = StringBuilder()
        val stack = Stack<Char>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]

            // Si el carácter es un operando, añadirlo a la salida
            if (c.isDigit() || c == '.') {
                while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                    result.append(expression[i++])
                }
                result.append(' ')
                i--
            } else if (c == '(') {
                stack.push(c)
            } else if (c == ')') {
                while (stack.isNotEmpty() && stack.peek() != '(') {
                    result.append(stack.pop()).append(' ')
                }
                stack.pop()
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == 'e' || c == 'r') {
                while (stack.isNotEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    result.append(stack.pop()).append(' ')
                }
                stack.push(c)
            }
            i++
        }

        // Extraer todos los operadores de la pila
        while (stack.isNotEmpty()) {
            result.append(stack.pop()).append(' ')
        }

        return result.toString()
    }

    // Función para evaluar una expresión postfija
    fun evaluatePostfix(expression: String): Double {
        val stack = Stack<Double>()
        val tokens = expression.split(" ").filter { it.isNotEmpty() }
        for (token in tokens) {
            if (token[0].isDigit() || token[0] == '.') {
                stack.push(token.toDouble())
            } else {
                val b = stack.pop()
                val a = if (stack.isNotEmpty() && token[0] != 'e') stack.pop() else 0.0
                stack.push(applyOp(a, b, token[0]))
            }
        }
        return stack.pop()
    }

    // Función para evaluar una expresión infija
    fun evaluate(expression: String): Double {
        return evaluatePostfix(infixToPostfix(expression))
    }
}
