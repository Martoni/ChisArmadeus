# ChisArmadeus
Chisel3 wrapper for FPGA Hardware bloc of Armadeus Boards, apf6sp, opos6ul_sp, apf27, apf51

# Install

# Requirement

* [sbt](https://www.scala-sbt.org/)
* [WbPlumbing](https://github.com/Martoni/WbPlumbing)

# Clone and publish local

Clone it on host then publish it locally:
```bash
$ git clone https://github.com/Martoni/ChisArmadeus.git
$ cd ChisArmadeus
$ sbt publishLocal
```

Add this line in your project build.sbt file :
```scala
libraryDependencies ++= Seq("org.armadeus" %% "chisarmadeus" % "0.1")
```


## Opos6ul_sp
All code for Opos6ul_sp is in the following file :
```
chisarmadeus/op6sptest/op6sptest.scala
```

* Imx6ulEim extends Bundle: Bundle declaration for EIM i.MX6ul interface.
* Imx6ulEimWbMaster extends Module: Wrapper to convert EIM to Wishbone master.
