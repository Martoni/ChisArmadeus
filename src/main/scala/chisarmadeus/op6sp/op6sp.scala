package chisarmadeus.op6sp

import chisel3._
import chisel3.util._

import wbplumbing.WbMaster

class Imx6ulEim extends Bundle {
  /* dain/daout/daen should be fusioned with triStateBuffer
   * on Top RawModule component */
  val dain  = Input(UInt(16.W))
  val daout = Output(UInt(16.W))
  val daen = Output(Bool())

  val lba = Input(Bool())
  val rw = Input(Bool())
  val cs = Input(Bool())

  val eb = Input(UInt(2.W))
  val oe = Input(Bool())
}

class Eim2Wishbone extends Module {
  val io = IO(new Bundle{
    val eim = new Imx6ulEim
    val wbm = new WbMaster(dwidth=16, awidth=16, iname = "Eim2Wishbone")
  })

  /* ack_i not supported by eim */

  val addressReg = RegInit(0.U(16.W))
  val writeData = RegInit(0.U(16.W))

  val write = Wire(Bool())
  val read  = Wire(Bool())
  val strobe= Wire(Bool())


  /* Update address register */
  when(!io.eim.lba){
    addressReg := 0.U(1.W) ## io.eim.dain(15, 1)
  }.otherwise{
    writeData := io.eim.dain
  }

  strobe := !io.eim.cs
  write  := !io.eim.cs && !io.eim.rw
  read   := !io.eim.cs && io.eim.rw

  io.wbm.adr_o := addressReg
  io.wbm.dat_o := Mux(write, writeData, 0.U)
  io.wbm.stb_o := strobe
  io.wbm.we_o  := write
  io.wbm.cyc_o := strobe

  io.eim.daout := io.wbm.dat_i
  io.eim.daen := Mux(read, true.B, false.B)

}

object Eim2Wishbone extends App {
  println("generate minimalistic verilog Eim2Wishbone module")
  chisel3.Driver.execute(Array[String](), () => new Eim2Wishbone())
}
