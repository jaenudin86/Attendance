# AttendanceApp
This app is built in Android which lets facilitator take attendance for students or attendees. Both facilitator and attendees will install this app. Facilitator can set up lectures according to their timetable in their app. Facilitator can generate random code or scan NFC tag and ask students to fill attendance. Students can submit their attendance by either filling the random code or scanning NFC code. Facilitator can view and export the reports about the students.

The MySQL database for this app is hosted at Amazone Cloud. Basic database schema and requirements are here:
https://github.com/rujoota/CSC780_Attendance/blob/master/Attendance%20App.docx

You can view initial screen design: https://github.com/rujoota/CSC780_Attendance/blob/master/CSC780_Attendance%20App.pdf

Also, its progress report (journal file for CSC 780 subject) can be found here:
https://github.com/rujoota/CSC780_Attendance/blob/master/journal.md

Some features of Android which this app uses
* Recycler view
* Remotely hosted API utilization for connecting to database on cloud
* Common settings menu for all activities
* Customized color-coded calendar
* File import-export from phone
* Common branding and coloring
* Checkbox drawable state selectors
* NFC communication
