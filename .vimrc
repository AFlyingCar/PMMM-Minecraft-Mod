let g:syntastic_java_checkers = ["javac"]
let g:syntastic_java_javac_classpath = "build/tmp/recompSrc/:src/main/java:build/tmp/decompile/*.jar:build/tmp/deobfBinJar/*.jar:build/tmp/deobfuscateJar/deobfed.jar:build/tmp/reobf/*.jar:build/libs/modid-1.0.jar:build/libs/mod_madokaMagica-1.0.jar"
let g:syntastic_check_on_open = 0
let g:syntastic_check_on_wq = 0
let g:syntastic_ignore_files = ["build/tmp/recompSrc/*"]

set colorcolumn=0

