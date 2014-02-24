/*
 * Copyright (C) February 2014 Caller Menu
 * Project for Tal Lavian, tlavian@gmail.com
 * @author Josef John, josefjohn88@gmail.com
 */

Intro / Description
This project is a prototype android application for Tal Lavian, tlavian@gmail.com to show the 
concept of calling companies and receiving the companies menus which the user can view 
instead of having to listen to each option. 
After the user makes selections, the call is then placed (with the selections added on to 
the end of the call) so the user can get to the place in the menu they want without 
wasting time listening each step of the way.

Issues / Warnings
Currently, the app makes calls to a Python Server (Django) hosted on Heroku to get company menus.
The request is only valid for certain numbers at the moment (hard coded in this app). This was 
done to prevent every call the user makes having to start up this application. I would advise 
keeping the hard coding of numbers on the app so the app does not need to contact the server 
when the user is not making a call to a company.

Also, the app now makes calls to 1800_______. If this was changed to +1800_______ or just 
800_______ (with '+' or without '1'), the server may not recognize the number. 
(This issue should be fixed at some point).

Developers
Up till February 2014, Josef John was the only developer so if you have questions about 
the project / code from anytime up to Feb 2014, email him @josefjohn88@gmail.com.

Note from Josef:
The initial focus on this project was a working prototype so I focused on functionality at 
the expense of documentation (not all the functionality is finalized even now) and test cases.
This app is functioning but may need some aspects changed as the project progresses / new ideas 
come up. Documentation is sparse and there are currently no test cases (only manual testing on 
a android device has been done thus far).




