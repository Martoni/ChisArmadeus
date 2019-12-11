package chisarmadeus.op6sp

import chisel3._
import chisel3.util._


class Imx6ulEim extends Bundle {
  /* daIn/daOut should be fusioned with triStateBuffer
   * on Top RawModule component */
  val daIn  = Input(UInt(16.W))
  val daOut = Output(UInt(16.W))
  val lba = Input(Bool())
  val rw = Input(Bool())
  val cs0 = Input(Bool())
  val eb = Input(UInt(2.W))
  val oe = Input(Bool())
}
