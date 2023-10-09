package com.sameh.simplecalculator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.sameh.simplecalculator.Constants.TAG
import com.sameh.simplecalculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val stringBuilder = StringBuilder()

    private var leftBracketOpened = 0

    private val operators = listOf('+', '-', '*', '/')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonsClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setButtonsClickListener() {
        val buttons = getAppButtons()

        buttons.forEach { button ->
            button.setOnClickListener {
                handleOnButtonClickListener(button.text.toString())
            }
        }
        binding.btnDeleteLast.setOnClickListener {
            deleteLastItem()
            updateErrorText("")
            updateOutputText(Constants.ZERO)
        }
    }

    private fun handleOnButtonClickListener(value: String) {
        updateOutputText(Constants.ZERO)
        updateErrorText("")
        when (value) {
            Constants.ADDITION,
            Constants.SUBTRACTION,
            Constants.MULTIPLICATION,
            Constants.DIVISION -> handleOperatorsClickListener(value.first())

            Constants.LEFT_BRACKET -> handleLeftBracketClickListener()
            Constants.RIGHT_BRACKET -> handleRightBracketClickListener()
            Constants.DOT -> handleDotClickListener()
            Constants.EQUAL -> calculateExpression()
            Constants.CLEAR -> clearExpression()
            else -> appendToExpression(value)
        }
    }

    private fun handleOperatorsClickListener(operator: Char) {
        if (stringBuilder.isNotEmpty()) {
            val lastIndex = stringBuilder.length - 1
            val lastChar = stringBuilder[lastIndex]
            if (lastChar in operators)
                setToExpression(lastIndex, operator)
            else
                appendToExpression(operator.toString())
        }
    }

    private fun handleLeftBracketClickListener() {
        appendToExpression(Constants.LEFT_BRACKET)
        leftBracketOpened++
    }

    private fun handleRightBracketClickListener() {
        if (leftBracketOpened >= 1) {
            appendToExpression(Constants.RIGHT_BRACKET)
        }
        if (leftBracketOpened > 0) {
            leftBracketOpened--
        }
    }

    private fun handleDotClickListener() {
        if (stringBuilder.isNotEmpty()) {
            val lastIndex = stringBuilder.length - 1
            val lastItem = stringBuilder[lastIndex]
            if (lastItem != Constants.DOT.first()) {
                appendToExpression(Constants.DOT)
            }
        }
    }

    private fun appendToExpression(value: String) {
        stringBuilder.append(value)
        updateInputText()
    }

    private fun setToExpression(index: Int, value: Char) {
        stringBuilder.setCharAt(index, value)
        updateInputText()
    }

    private fun calculateExpression() {
        try {
            val expression: Expression = ExpressionBuilder(stringBuilder.toString()).build()
            val result = expression.evaluate()
            updateOutputText(result.toString())
            updateErrorText("")
        } catch (e: Exception) {
            Log.d(TAG, "calculateExpression: ${e.message.toString()}")
            updateOutputText(Constants.ZERO)
            updateErrorText(e.message.toString())
        }
    }

    private fun updateOutputText(value: String) {
        binding.tvOutput.text = value
    }

    private fun updateErrorText(errorMes: String) {
        binding.tvError.text = errorMes
    }

    private fun updateInputText() {
        binding.tvInput.text = stringBuilder.toString()
        //calculateExpression()
    }

    private fun clearExpression() {
        stringBuilder.clear()
        updateInputText()
        updateErrorText("")
        updateOutputText(Constants.ZERO)
    }

    private fun deleteLastItem() {
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            updateInputText()
        }
    }

    private fun getAppButtons(): List<MaterialButton> =
        arrayListOf(
            binding.btn0,
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9,
            binding.btnDot,
            binding.btnClear,
            binding.btnLeftBracket,
            binding.btnRightBracket,
            binding.btnDivision,
            binding.btnMultiplication,
            binding.btnSubtraction,
            binding.btnAddition,
            binding.btnEquals
        )

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(Constants.INPUT_KEY, stringBuilder.toString())
        outState.putString(Constants.ERROR_MES_KEY, binding.tvError.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val inputText = savedInstanceState.getString(Constants.INPUT_KEY)
        val errorMes = savedInstanceState.getString(Constants.ERROR_MES_KEY)

        Log.d(TAG, "onRestoreInstanceState: $inputText")
        if (!inputText.isNullOrEmpty()) {
            appendToExpression(inputText)
            calculateExpression()
        }

        Log.d(TAG, "onRestoreInstanceState: $errorMes")
        if (!errorMes.isNullOrEmpty())
            updateErrorText(errorMes)
    }
}