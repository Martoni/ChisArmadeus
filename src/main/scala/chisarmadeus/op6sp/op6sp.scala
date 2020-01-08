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

  io.wbm.adr_o := addressReg
  io.wbm.dat_o := io.eim.dain
  io.eim.daout := io.wbm.dat_i

  /* Update address register */
  when(!io.eim.lba){
    addressReg := io.eim.dain
  }

  io.eim.daen := false.B
  io.wbm.we_o := false.B
  io.wbm.stb_o := false.B
  io.wbm.cyc_o := false.B
  when(!io.eim.cs){
    io.wbm.stb_o := true.B
    io.wbm.cyc_o := true.B
    when(!io.eim.rw){ // reading
      io.eim.daen := true.B
      io.eim.daout := io.wbm.dat_i
    }.otherwise{ // writing
      io.wbm.we_o := true.B
    }
  }

}

object Eim2Wishbone extends App {
  println("generate minimalistic verilog Eim2Wishbone module")
  chisel3.Driver.execute(Array[String](), () => new Eim2Wishbone())
}
