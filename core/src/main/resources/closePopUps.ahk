#NoTrayIcon
#Persistent
#SingleInstance force

SetTitleMatchMode, 1
SetTimer, ClosePopUp, %2%
return

ClosePopUp:
WinKill, %1%
return