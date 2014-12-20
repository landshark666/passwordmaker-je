# passwordmaker-je

A Java port of the Firefox PasswordMaker plugin.  PasswordMaker is a password management program which does not store any actual passwords. Instead the way passwords are generated are described and you input a master password which feeds into that algorithm to generate the final password. Each site can have different ways to generate the password, or all sites can share the same settings.

The latest version is *0.7.3*.

I've moved the codebase from Google Code to github. The screenshots are still on the Google Code site (links below).

### On Windows 7
![Windows Screenshot](http://wiki.passwordmaker-je.googlecode.com/git/images/main-win32.png)

### On OSX 10.7
![OSX Screenshot](http://wiki.passwordmaker-je.googlecode.com/git/images/main-osx.png)

### Account Dialog
![Account Dlg](http://wiki.passwordmaker-je.googlecode.com/git/images/accountdlg-win32.png)

### Filtered Accounts
![Windows Filtered](http://wiki.passwordmaker-je.googlecode.com/git/images/main-filtered-win32.png)

### URL Search
![URL Search](http://wiki.passwordmaker-je.googlecode.com/git/images/main-urlsearch-win32.png)

## Features
  * Read/write RDF files. RDF is the format the Firefox plugin uses. You can re-import changes saved from PasswordMaker-JE into the Firefox plugin.
  * Sorting of accounts (alphabetical ascending/descending, folders on top/bottom).
  * Cross platform.  Anywhere that SWT has been ported to, PasswordMaker-JE can run (once a jar is built for it).
  * Supports all the non-buggy versions of hash algorithms from the Firefox plugin.
    * MD4
    * HMAC-MD4
    * MD5
    * HMAC-MD5
    * SHA-1
    * HMAC-SHA1
    * SHA-256
    * HMAC-SHA-256
    * RIPEMD160
    * HMAC-RIPEMD160
  * Search by URL. Copy and paste your browser's URL into the URL search box to search the list of account patterns for a match (similar to how the Firefox plugin locates accounts based on the current page).
  * Filtering.  If you have alot of accounts, you can use the filter box to narrow down the list of visible accounts.
  * The interface is very similar to the Firefox plugin.
  * Command-line support (limited at the moment).

Please see the [Usage] page for information on how to use the program.
