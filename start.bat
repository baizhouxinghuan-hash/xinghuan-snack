@echo off
chcp 65001 >nul
setlocal

rem ============================================
rem  星幻爬虫 StarMirage Crawler 启动脚本
rem ============================================
cd /d "%~dp0"

if not exist "target\classes" mkdir "target\classes"

echo [1/2] 编译源码...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d target\classes -cp "lib\*" @sources.txt
if errorlevel 1 (
    echo 编译失败，请检查上方错误信息。
    del sources.txt >nul 2>nul
    pause
    exit /b 1
)
del sources.txt >nul 2>nul

echo [2/2] 启动图形界面...
java -Dfile.encoding=UTF-8 -cp "target\classes;lib\*" xiaobaisnack.crawler.app.MainApplication --gui

pause