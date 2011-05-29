@echo off
echo GIT shite commit utility, designed because
echo I am too incompetent to figure out how to setup egit properly
echo enter commit message:
set /p commitmessage=
echo %commitmessage%

git add .
git commit -m "%commitmessage%"
git push