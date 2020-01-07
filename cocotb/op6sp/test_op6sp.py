#! /usr/bin/python3
# -*- coding: utf-8 -*-
#-----------------------------------------------------------------------------
# Author:   Fabien Marteau <fabien.marteau@armadeus.com>
# Created:  06/01/2020
#-----------------------------------------------------------------------------
""" test_wbgpio
"""
import os
import sys
import cocotb
import logging
from cocotb import SimLog
from cocotb.triggers import Timer
from cocotb.result import raise_error
from cocotb.result import TestError
from cocotb.result import ReturnValue
from cocotb.clock import Clock
from cocotb.triggers import Timer
from cocotb.triggers import RisingEdge
from cocotb.triggers import FallingEdge
from cocotb.triggers import ClockCycles
from cocotb.binary import BinaryValue

from cocomod.wishbone.monitor import WishboneSlave
from cocotbext.imxeim.driver import EIMMaster

class Eim2Wishbone(object):
    """
    """
    LOGLEVEL = logging.INFO

    # clock frequency is 50Mhz
    PERIOD = (20, "ns")

    STATUSADDR = 0
    DIRADDR    = 1
    READADDR   = 2
    WRITEADDR  = 3

    def __init__(self, dut):
        if sys.version_info[0] < 3:
            raise Exception("Must be using Python 3")
        self._dut = dut
        self.log = SimLog("Eim2Wishbone.{}".format(self.__class__.__name__))
        self._dut._log.setLevel(self.LOGLEVEL)
        self.log.setLevel(self.LOGLEVEL)
        self.clock = Clock(self._dut.clock, self.PERIOD[0], self.PERIOD[1])
        self._clock_thread = cocotb.fork(self.clock.start())
        self.log.info("instanciate slave")
        self.wbm = WishboneSlave(dut, "io_wbm", dut.clock,
                          width=16,   # size of data bus
                          signals_dict={"cyc":  "cyc_o",
                                      "stb":  "stb_o",
                                      "we":   "we_o",
                                      "adr":  "adr_o",
                                      "datwr":"dat_o",
                                      "datrd":"dat_i",
                                      "ack":  "ack_i" })

        self.eim = EIMMaster(dut, "io_eim", dut.clock, width=16)

    @cocotb.coroutine
    def reset(self):
        self._dut.reset <= 1
        short_per = Timer(100, units="ns")
        yield short_per
        self._dut.reset <= 1
        yield short_per
        self._dut.reset <= 0
        yield short_per

@cocotb.test()#skip=True)
def test_simple(dut):
    eim = Eim2Wishbone(dut)
    dut.log.info("reset bus")
    yield eim.reset()
    yield Timer(1, units="us")
