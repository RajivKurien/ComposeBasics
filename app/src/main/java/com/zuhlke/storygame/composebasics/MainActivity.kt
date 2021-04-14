package com.zuhlke.storygame.composebasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.zuhlke.storygame.composebasics.ui.theme.ComposeBasicsTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    val viewModel by viewModels<ListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApp { MyScreenContent() }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    ComposeBasicsTheme(darkTheme = false) {
        content()
    }
}

@Composable
fun Greeting(name: NameState, onClick: (NameState) -> Unit) {
    val backgroundColor by animateColorAsState(if (name.isSelected) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.secondary)

    Surface(
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(name) })
    ) {
        Text(
            text = "Hello ${name.value}!",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.h5
        )
    }
}

@Composable
fun MyScreenContent(names: List<NameState> = List(1000) { NameState(it, "Hello Android #$it") }) {
    val counterState = remember { mutableStateOf(0) }
    val namesState by remember { mutableStateOf(names) }

    Column(modifier = Modifier.fillMaxHeight()) {
        NameList(
            names = namesState,
            modifier = Modifier.weight(1f)
        ) { name ->
            namesState.find { it.id == name.id }?.let {
                it.isSelected = !it.isSelected
            }
        }
        Counter(
            counterState.value,
            onClick = { counterState.value = it },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp)
        )
    }
}

@Composable
fun NameList(names: List<NameState>, modifier: Modifier = Modifier, onClick: (NameState) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(items = names) { name ->
            Greeting(name, onClick)
            Divider(color = MaterialTheme.colors.onBackground)
        }
    }
}

data class NameState(val id: Int, val value: String, var isSelected: Boolean = false)

@Composable
fun Counter(
    count: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(count + 1) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (count > 5) MaterialTheme.colors.primary
            else MaterialTheme.colors.secondary
        )
    ) {
        Text(
            "I've been clicked $count times",
            color = if (count > 5) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        MyScreenContent()
    }
}



class ListViewModel(names: List<String> = List(1000) { "Hello Android #$it" }) :
    ViewModel() {

    private val state: MutableStateFlow<List<NameState>> = MutableStateFlow(
        names.mapIndexed { index, name -> NameState(index, name) }
    )

    fun observerState(): StateFlow<List<NameState>> = state.asStateFlow()

    fun onClick(id: Int) {
        state.value[id].let {
            it.isSelected = !it.isSelected
        }
    }
}