#Puella Magi Minecraft Magica

##Description

Puella Magi Minecraft Magica is a mod for [Minecraft](minecraft.net) which strives to bring the world of Puella Magi Madoka Magica into Minecraft, by providing the ability to be granted a wish, and become a Puella Magi and fight evil witches. This mod will have heavy spoilers for Puella Magi Madoka Magica, so be sure to watch it before downloading and playing this mod.

##Build Instructions

###Important Note:
Building this mod requires heavy use of a command-prompt. I will assume that everyone who reads this understands how to use a command line as well as the basics of Java programming.

###A note to Windows users:
Whenever I refer to `./gradlew`, remember that it is different for those using Windows. Make sure that you type `gradlew.bat` instead of `./gradlew` (Don't forget the options that come after it though).

1. Clone the repository onto your computer. You may do this either through Git: `git clone https://www.github.com/AFlyingCar/PMMM-Minecraft-Mod.git`, or by downloading and extracting the repository [as a zip file](https://github.com/AFlyingCar/PMMM-Minecraft-Mod/archive/master.zip).
2. Open your preferred terminal (Command Line on Windows), and navigate it to the folder where you just placed the repository. If you type `ls` (`dir` on Windows), you should see something similar to this:
![](http://i.imgur.com/n9A1NRQ.png)
3. Before you can build, you must first setup the dependencies. In your terminal, type `./gradlew setupDecompWorkspace`.
4. Once that is done, you should be able to compile the mod by typing `./gradlew build`. 
5. Finally, you can test the mod by running `./gradlew runClient` (Note: This will also build the mod if you haven't run the `build` task yet).
