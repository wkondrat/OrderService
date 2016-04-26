/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luciow.warehouse;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import pl.luciow.warehouse.impl.OrderServiceImpl;
import pl.luciow.warehouse.impl.WarehouseImpl;
import pl.luciow.warehouse.model.Item;
import pl.luciow.warehouse.model.Mail;
import pl.luciow.warehouse.model.NotEnoughItemsException;
import pl.luciow.warehouse.model.Order;
import pl.luciow.warehouse.model.OrderProcessException;
import pl.luciow.warehouse.model.Payment;

public class OrderServiceTest {

	private OrderService orderService;

	@Test
	public void fillOrderSuccesTest() throws NotEnoughItemsException, OrderProcessException {
		Warehouse warehouseMock = Mockito.mock(Warehouse.class);
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		try {
			Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenReturn(null);
			orderService.fillOrder(new Order());
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}

	@Test(expected = OrderProcessException.class)
	public void fillOrderThrowTest() throws NotEnoughItemsException, OrderProcessException {
		Warehouse warehouseMock = Mockito.mock(Warehouse.class);
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		try {
			Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenThrow(new NotEnoughItemsException());
			orderService.fillOrder(new Order());
		} catch (NotEnoughItemsException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}

	@Test
	public void cancelOrderTest() throws OrderProcessException {
		Warehouse warehouseMock = Mockito.mock(WarehouseImpl.class);
		Order order = new Order();
		order.setItems(new ArrayList<Item>());
		order.getItems().add(new Item());
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		try {
			Mockito.doCallRealMethod().when(warehouseMock).addItems(Mockito.any(List.class));
			orderService.cancelOrder(order);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}

	@Test
	public void processPaymentThrowTest() throws Exception {
		class MyArgumentMatcher extends ArgumentMatcher<Object> {
			public boolean matches(Object mail) {
				return ((Mail) mail).getContent().equals("Error occured");
			}
		}
		PaymentService paymentService = Mockito.mock(PaymentService.class);
		MailService mailServiceMock = Mockito.mock(MailService.class);
		orderService = new OrderServiceImpl(mailServiceMock, paymentService, null);
		Order order = new Order();
		Payment payment = new Payment();
		Mockito.when(paymentService.processPayment(Mockito.any(Payment.class))).thenThrow(new Exception());
		orderService.processPayment(order, payment);
		Mockito.verify(mailServiceMock).sendMail((Mail) Mockito.argThat(new MyArgumentMatcher()));
	}

	@Test
	public void processPaymentSuccessTest() throws Exception {
		class MyArgumentMatcher extends ArgumentMatcher<Object> {
			public boolean matches(Object mail) {
				return ((Mail) mail).getContent().equals("Success");
			}
		}
		PaymentService paymentService = Mockito.mock(PaymentService.class);
		MailService mailServiceMock = Mockito.mock(MailService.class);
		orderService = new OrderServiceImpl(mailServiceMock, paymentService, null);
		Order order = new Order();
		Payment payment = new Payment();
		Long longVal = new Long(2);
		Mockito.when(paymentService.processPayment(Mockito.any(Payment.class))).thenReturn(longVal);
		orderService.processPayment(order, payment);
		Mockito.verify(mailServiceMock).sendMail((Mail) Mockito.argThat(new MyArgumentMatcher()));
	}

}
