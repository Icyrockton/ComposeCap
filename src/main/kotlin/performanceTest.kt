import androidx.compose.desktop.Window
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

/**
 *@Author : Icyrockton
 *@Date: 2021/5/11 21:00
 **/


fun main() = Window {
    val coroutineScope = rememberCoroutineScope()
    val list = remember { mutableStateListOf<Int>() }
    LaunchedEffect(true){
        coroutineScope.launch {
            while (true){
                list.clear()
                repeat(2000){
                    list.add((Math.random() * 1000).toInt())
                }
                delay(3000L)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.fillMaxSize().padding(end = 12.dp).verticalScroll(scrollState)) {
            list.forEach {
                Text(it.toString())
            }


        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}