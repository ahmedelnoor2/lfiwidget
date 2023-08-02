import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class CoinData {
  String symbol;
  double open;
  double close;
  late double percentage;
  bool isFavorite;

  CoinData(this.symbol, this.open, this.close)
      : percentage = ((close - open) / open) * 100,
        isFavorite = false;
}

class CoinWidget extends StatefulWidget {
  @override
  _CoinWidgetState createState() => _CoinWidgetState();
}

class _CoinWidgetState extends State<CoinWidget>
    with SingleTickerProviderStateMixin {
  List<CoinData> coins = [];
  TabController? _tabController;

  @override
  void initState() {
    super.initState();
    fetchData();
    _tabController = TabController(length: 2, vsync: this);
  }

  Future<void> fetchData() async {
    // Replace the URL with the appropriate endpoint for your API
    String baseUrl = "https://openapi.lyotrade.com/sapi/v1/klines";
    List<String> coinSymbols = ["BTCUSDT", "LFI1USDT", "CLFIUSDT"];
    for (String symbol in coinSymbols) {
      String url = "$baseUrl?symbol=$symbol&interval=1day&limit=1";
      try {
        final response = await http.get(Uri.parse(url));
        if (response.statusCode == 200) {
          final data = jsonDecode(response.body);
          CoinData coinData = CoinData(
            _formatSymbol(symbol),
            double.parse(data[0]['open']),
            double.parse(data[0]['close']),
          );
          coins.add(coinData);
        }
      } catch (e) {
        print("Error: $e");
      }
    }
    setState(() {});
  }

  String _formatSymbol(String symbol) {
    // Assuming the symbol format is "XXXUSDT"
    return symbol.substring(0, 3) + " / " + symbol.substring(3);
  }

  void _toggleFavorite(int index) {
    setState(() {
      coins[index].isFavorite = !coins[index].isFavorite;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Coin Data"),
        bottom: TabBar(
          controller: _tabController,
          tabs: [
            Tab(text: "Coins"),
            Tab(text: "Favorites"),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          _buildCoinList(),
          _buildFavoritesList(),
        ],
      ),
    );
  }

  Widget _buildCoinList() {
    return ListView.builder(
      itemCount: coins.length,
      itemBuilder: (context, index) {
        CoinData coin = coins[index];
        return Card(
          color: Colors.black,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(40),
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        coin.symbol,
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                          fontSize: 14.0,
                        ),
                      ),
                      IconButton(
                        onPressed: () => _toggleFavorite(index),
                        iconSize: 16.0,
                        icon: Icon(
                          coin.isFavorite ? Icons.star : Icons.star_border,
                          color: Colors.yellow,
                        ),
                      ),
                    ],
                  ),
                ),
                SizedBox(width: 8.0),
                Text(
                  "${coin.close}",
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 16.0,
                  ),
                ),
                SizedBox(width: 8.0),
                Container(
                  padding: EdgeInsets.all(4.0),
                  color: coin.percentage >= 0 ? Colors.green : Colors.red,
                  child: Text(
                    "${coin.percentage.toStringAsFixed(2)}%",
                    style: TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 14.0,
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildFavoritesList() {
    final favoriteCoins = coins.where((coin) => coin.isFavorite).toList();
    return ListView.builder(
      itemCount: favoriteCoins.length,
      itemBuilder: (context, index) {
        CoinData coin = favoriteCoins[index];
        return Card(
          color: Colors.black,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(40),
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  coin.symbol,
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                    fontSize: 14.0,
                  ),
                ),
                Text(
                  "${coin.close}",
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 16.0,
                  ),
                ),
                IconButton(
                  onPressed: () => _toggleFavorite(coins.indexOf(coin)),
                  icon: Icon(
                    Icons.star,
                    color: Colors.yellow,
                  ),
                ),
                Container(
                  padding: EdgeInsets.all(4.0),
                  color:
                  coin.percentage >= 0 ? Colors.green : Colors.red,
                  child: Text(
                    "${coin.percentage.toStringAsFixed(2)}%",
                    style: TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: 14.0,
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

void main() {
  runApp(MaterialApp(
    debugShowCheckedModeBanner: false,
    home: CoinWidget(),
  ));
}
