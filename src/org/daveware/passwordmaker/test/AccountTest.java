package org.daveware.passwordmaker.test;

import junit.framework.Assert;

import org.daveware.passwordmaker.Account;
import org.daveware.passwordmaker.Account.UrlComponents;
import org.junit.Test;

public class AccountTest {

    @Test
    public void testCopySettings() {
        // Verify that the set of UrlComponents created during Account.copySettings()
        // is actually a copy.
        Account orig = new Account();
        Account copy = new Account();

        orig.addUrlComponent(UrlComponents.Domain);
        copy.copySettings(orig);
        copy.clearUrlComponents();
        Assert.assertFalse(orig.getUrlComponents().isEmpty());
        
        // Verify that copySettings works when the set of components is empty.
        copy.addUrlComponent(UrlComponents.Protocol);
        orig.clearUrlComponents();
        copy.copySettings(orig);
        Assert.assertTrue(copy.getUrlComponents().isEmpty());
    }

}
