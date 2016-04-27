/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.luciow.warehouse;

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
		// given
		Warehouse warehouseMock = Mockito.mock(Warehouse.class);
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		// when
		Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenReturn(null);
		// then
		orderService.fillOrder(new Order());
	}

	@Test(expected = OrderProcessException.class)
	public void fillOrderThrowTest() throws NotEnoughItemsException, OrderProcessException {
		// given
		Warehouse warehouseMock = Mockito.mock(Warehouse.class);
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		// when
		Mockito.when(warehouseMock.removeItems(Mockito.any(List.class))).thenThrow(new NotEnoughItemsException());
		// then
		orderService.fillOrder(new Order());
	}

	@Test
	public void cancelOrderTest() throws OrderProcessException {
		// given
		Warehouse warehouseMock = Mockito.mock(WarehouseImpl.class);
		Order order = new Order();
		order.setItems(new ArrayList<Item>());
		order.getItems().add(new Item());
		orderService = new OrderServiceImpl(null, null, warehouseMock);
		// when
		Mockito.doCallRealMethod().when(warehouseMock).addItems(Mockito.any(List.class));
		// then
		orderService.cancelOrder(order);
		Mockito.verify(warehouseMock, Mockito.times(1)).addItems(Mockito.any(List.class));
	}

	@Test
	public void processPaymentThrowTest() throws Exception {
		// given
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
		// when
		Mockito.when(paymentService.processPayment(Mockito.any(Payment.class))).thenThrow(new Exception());
		// then
		orderService.processPayment(order, payment);
		Mockito.verify(mailServiceMock).sendMail((Mail) Mockito.argThat(new MyArgumentMatcher()));
	}

	@Test
	public void processPaymentSuccessTest() throws Exception {
		// given
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
		// when
		Mockito.when(paymentService.processPayment(Mockito.any(Payment.class))).thenReturn(longVal);
		// then
		orderService.processPayment(order, payment);
		Mockito.verify(mailServiceMock).sendMail((Mail) Mockito.argThat(new MyArgumentMatcher()));
	}

}
