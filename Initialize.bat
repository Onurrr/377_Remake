@echo Off
title Server
java -Xmx512m -XX:+DisableExplicitGC -noverify -cp bin;libs/* com.rs2.Server
pause