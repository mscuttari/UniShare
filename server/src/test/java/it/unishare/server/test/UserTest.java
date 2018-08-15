package it.unishare.server.test;

import it.unishare.server.Server;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void emailValidity() {
        try {
            Server server = new Server(null, 0);
            Method method = Server.class.getDeclaredMethod("isEmailValid", String.class);
            method.setAccessible(true);

            assertTrue((boolean)method.invoke(server, "me@gmail.com"));
            assertTrue((boolean)method.invoke(server, "me@me.co.uk"));
            assertTrue((boolean)method.invoke(server, "me@yahoo.com"));
            assertTrue((boolean)method.invoke(server, "me-100@me.com"));
            assertTrue((boolean)method.invoke(server, "me.100@me.net"));
            assertTrue((boolean)method.invoke(server, "me.100@me.com.au"));
            assertTrue((boolean)method.invoke(server, "me@1.com"));
            assertTrue((boolean)method.invoke(server, "me+100@gmail.com"));
            assertTrue((boolean)method.invoke(server, "me-100@yahoo-test.com"));

            assertFalse((boolean)method.invoke(server, "me@.com.me"));
            assertFalse((boolean)method.invoke(server, "me123@.com"));
            assertFalse((boolean)method.invoke(server, "me123@.com.com"));
            assertFalse((boolean)method.invoke(server, "me..2002@gmail.com"));
            assertFalse((boolean)method.invoke(server, "me.@gmail.com"));
            assertFalse((boolean)method.invoke(server, "me@me@gmail.com"));

        } catch (RemoteException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
