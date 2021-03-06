package exchange;

import java.util.HashMap;
import java.util.Map;

import exchange.dto.Order;
import exchange.exchanges.Exchange;
import exchange.reader.Reader;
import exchange.reader.StandardInputReader;
import exchange.writer.StandardOutputWriter;
import exchange.writer.Writer;

public class ExchangeApp {
	
	public static void main(String[] args) {
		Map<String, Exchange<Order>> exchanges = new HashMap<>();
		Reader<Order> reader = new StandardInputReader();
		Writer<Order> writer = new StandardOutputWriter();
		ExchangeService<Order> exchangeService = new ExchangeService<>(reader, exchanges, writer, Order::getItemType);
		exchangeService.run();
	}

}
