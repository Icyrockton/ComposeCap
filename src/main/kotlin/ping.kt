import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.viewModel
import traffic.TrafficCircularProgressBar

/**
 *@Author : Icyrockton
 *@Date: 2021/5/10 11:39
 **/
@Composable
fun Fee() {
    val totalTraffic by viewModel.pcap.totalTraffic.collectAsState()
    var feeRate by remember { mutableStateOf("0") }
    Row(modifier = Modifier.padding(top = 400.dp).height(200.dp).fillMaxWidth()) {
        TrafficCircularProgressBar(
            title = "流量",
            currentValue = totalTraffic / 1024 / 1024,
            maxValue = 1000,
            unit = "MB",
            modifier = Modifier.weight(0.25f),
            backGroundColor = Color(0xFFFFBD9E),
            color = Color(0xFFFF5200)
        )
        Card(modifier = Modifier.fillMaxHeight().weight(0.75f).background(Color.Gray)) {

            Column(modifier = Modifier.padding(start = 10.dp),verticalArrangement = Arrangement.Center) {
                Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "费率设置(每MB的价格)",Modifier.width(200.dp))
                    OutlinedTextField(value = feeRate, onValueChange = { feeRate = it },modifier = Modifier.padding(start = 20.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "费用",Modifier.width(200.dp))
                    OutlinedTextField(value = "${if(feeRate.isEmpty()) 0 else feeRate.toDouble() * (totalTraffic / 1024 / 1024)} RMB",onValueChange = {},modifier = Modifier.padding(start = 20.dp),readOnly = true)
                }
            }


        }
    }
}