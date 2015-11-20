This project is about creating an app for taking attendance. It can be utilized for normal courses as well as seminars/events.

Journal entry for 23rd Sep, 2015
What I have done:
->setup android studio environment
->setup geny motion
->design document done
->started on creating facutly activity and manage holiday activity
->finalized database design

Plans for next week:
->finish manage holiday activity
->start creating database in android

Problems:
->geny motion not working: solution- genymotion plugin for android studio
->calendar picker not in-built in android, need to figure out way to do time and date picker.


Journal entry for 1st Oct, 2015
What I have done:
->Created database
->Connected with php web service
->tried with parse object
->Started on holidays & course activity

Plans for next week:
->Finish testing with parse object
->Finish holidays and course page

Problems:
->Parse object, how to set up relations between tables?
->Web service in php setup, used existing SE cloud

Journal entry for 7th Oct, 2015
What I have done:
->Created own amazon cloud
->Finished CRUD for courses
->Finished CRUD for holidays
->Finished common menu

Plans for next week:
->Calendar CRUD

Problems:
->Setting up cloud with database
->Dealing with different date formats and calendar in holidays
->List view adapters

Journal entry for 15th Oct, 2015
What I have done:
->Created customized calendar
->created add calendar activity with selecter xml - defining state of checkboxed buttons
->added animations, alert dialog on delete and progress spinner while item is loading

Plan for next week:
Finishing my time table
Starting with attendance filling part

Problems:
Custom calendar from almost start - extensive testing and bug fixing required. Saw some ready-to-use components but not suitable for my needs
Fixing old code of holidays and courses-added startactivityforresult in add and edit.


Journal entry for 22nd Oct, 2015
What I have done:
Simplified navigation
Recyclerview
Import students
Calendar view updates

Plans for next week:
Fill attendance part

Problems:
File importing from Android


Journal entry for 29th Oct, 2015
What I have done:
Finished submit attendance module for faculty
Updated attendance calendar grid to reflect different colors

Plans for next week:
Complete faculty module with report exporting
Start on student module
Research about login

Problems:
Communicating with radio buttons of recycler adapter via activity(they do not retain their state)

Journal entry for 5th Nov, 2015
What I have done:
Finished fill attendance for student
Started on viewing reports for faculty

Plans for next week:
Start on login
Finish view reports

Problems:
Managing simultenous communication when student submits attendance and when faculty receives in their app.

Journal entry for 11th Nov, 2015
What I have done:
Created basic app for NFC tag reading writing
Finished view report

Plans for next week:
Login
Multiple data writing in NFC tag

Problems:
Understanding how NFC works

Journal entry for 19th Nov, 2015
What I have done:
Tried NFC tag with custom mime type
Implemented login
Integrated NFC in attendance app

Plans for next week:
Custom mime type
New user signup
Adding intent-filters so that activity dynamically pops up correct activity when tag is scanned based on current logged in user.

Problems:
Custom mime type always throws errors
