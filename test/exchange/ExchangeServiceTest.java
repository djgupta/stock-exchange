package exchange;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import exchange.dto.Order;
import exchange.exchanges.Exchange;
import exchange.exchanges.MainExchange;
import exchange.reader.Reader;
import exchange.reader.StandardInputReader;
import exchange.writer.StandardOutputWriter;
import exchange.writer.Writer;

class ExchangeServiceTest {

	Map<String, Exchange<Order>> exchanges = new HashMap<>();
	Reader<Order> reader = new StandardInputReader();
	Writer<Order> writer = new StandardOutputWriter();
	ExchangeService<Order> exchangeService = new ExchangeService<>(reader, exchanges, writer, Order::getItemType);
	
	@Test
	@DisplayName("only one order")
	void test1() {
		Order order1 = new Order();
		order1.setOrderId("d1");
		order1.setItemType("apple");
		order1.setPrice(30);
		order1.setQuantity(100);
		order1.setTime(LocalTime.now());
		Order result1 = exchangeService.addAndExecute(new MainExchange(), order1);
		assertNull(result1);
	}
	
	@Test
	@DisplayName("two orders which get fully matched")
	void test2() {
		Exchange<Order> exchange = new MainExchange();
		Order order1 = new Order();
		order1.setOrderId("d1");
		order1.setItemType("apple");
		order1.setPrice(30);
		order1.setQuantity(100);
		order1.setTime(LocalTime.now());
		exchangeService.add(exchange, order1);
		
		Order order2 = new Order();
		order2.setOrderId("s1");
		order2.setItemType("apple");
		order2.setPrice(30);
		order2.setQuantity(100);
		order2.setTime(LocalTime.now());
		
		exchangeService.add(exchange, order2);
		List<Order> result =  exchangeService.executeAll(exchange);
		assertEquals(1, result.size());
		assertEquals("s1", result.get(0).getMatchedOrderId());
	}
	
	@Test
	@DisplayName("three orders which get matched")
	void test3() {
		Exchange<Order> exchange = new MainExchange();
		Order order1 = new Order();
		order1.setOrderId("d1");
		order1.setItemType("apple");
		order1.setPrice(30);
		order1.setQuantity(100);
		order1.setTime(LocalTime.now());
		exchangeService.add(exchange, order1);
		
		Order order2 = new Order();
		order2.setOrderId("s1");
		order2.setItemType("apple");
		order2.setPrice(30);
		order2.setQuantity(70);
		order2.setTime(LocalTime.now());
		exchangeService.add(exchange, order2);
		
		Order order3 = new Order();
		order3.setOrderId("s2");
		order3.setItemType("apple");
		order3.setPrice(30);
		order3.setQuantity(30);
		order3.setTime(LocalTime.now());
		exchangeService.add(exchange, order3);
		
		List<Order> result =  exchangeService.executeAll(exchange);
		assertEquals(2, result.size());
		assertEquals("s1", result.get(0).getMatchedOrderId());
		assertEquals("s2", result.get(1).getMatchedOrderId());
	}
	
	
	@Test
	@DisplayName("orders are placed where difference between demand price and supply price is highest ")
	void test4() {
		Exchange<Order> exchange = new MainExchange();
		Order order1 = new Order();
		order1.setOrderId("d1");
		order1.setItemType("apple");
		order1.setPrice(80);
		order1.setQuantity(100);
		order1.setTime(LocalTime.now());
		exchangeService.add(exchange,order1);
		
		Order order2 = new Order();
		order2.setOrderId("d2");
		order2.setItemType("apple");
		order2.setPrice(30);
		order2.setQuantity(100);
		order2.setTime(LocalTime.now());
		exchangeService.add(exchange,order2);
		
		Order order3 = new Order();
		order3.setOrderId("s1");
		order3.setItemType("apple");
		order3.setPrice(30);
		order3.setQuantity(100);
		order3.setTime(LocalTime.now());
		exchangeService.add(exchange,order3);
		
		Order order4 = new Order();
		order4.setOrderId("s2");
		order4.setItemType("apple");
		order4.setPrice(10);
		order4.setQuantity(100);
		order4.setTime(LocalTime.now());
		exchangeService.add(exchange, order4);
		
		List<Order> result =  exchangeService.executeAll(exchange);
		assertEquals(2, result.size());
		assertEquals("s2", result.get(0).getMatchedOrderId());
		assertEquals(order4.getPrice(), result.get(0).getPrice());
		assertEquals("s1", result.get(1).getMatchedOrderId());
		assertEquals(order3.getPrice(), result.get(1).getPrice());
	}
	

}
