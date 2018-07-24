import pir._
import pir.node._
import arch._
import prism.enums._

object TPCHQ6__N_1048576_ts_65536_op_1 extends PIRApp {
  def main(implicit design:PIRDesign) = {
    import design.pirmeta._
    val x2113 = withCtrl(design.top.topController) { ArgIn(init=0).name("x2113").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:23:25:dataSize") } // ArgInNew(Const(0))
    isAccum(x2113) = false
    bufferDepthOf(x2113) = 1
    boundOf(x2113) = 1048576
    val x2115 = withCtrl(design.top.topController) { ReadMem(x2113).name("x2115").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:26:28") } // RegRead(x2113)
    val x2116 = withCtrl(design.top.topController) { DRAM(dims=List(x2115)).name("x2116").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:26:27:dates") } // x2116 = DRAMNew(ArrayBuffer(x2115),Const(0))
    val x2117 = withCtrl(design.top.topController) { ReadMem(x2113).name("x2117").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:27:28") } // RegRead(x2113)
    val x2118 = withCtrl(design.top.topController) { DRAM(dims=List(x2117)).name("x2118").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:27:27:quants") } // x2118 = DRAMNew(ArrayBuffer(x2117),Const(0))
    val x2119 = withCtrl(design.top.topController) { ReadMem(x2113).name("x2119").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:28:26") } // RegRead(x2113)
    val x2120 = withCtrl(design.top.topController) { DRAM(dims=List(x2119)).name("x2120").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:28:25:discts") } // x2120 = DRAMNew(ArrayBuffer(x2119),Const(0))
    val x2121 = withCtrl(design.top.topController) { ReadMem(x2113).name("x2121").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:29:26") } // RegRead(x2113)
    val x2122 = withCtrl(design.top.topController) { DRAM(dims=List(x2121)).name("x2122").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:29:25:prices") } // x2122 = DRAMNew(ArrayBuffer(x2121),Const(0))
    val x2123 = withCtrl(design.top.topController) { ArgOut(init=0).name("x2123").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:32:21:out") } // ArgOutNew(Const(0))
    isAccum(x2123) = false
    bufferDepthOf(x2123) = 1
    val x2253 = withCtrl(design.top.topController) { UnitController(style=SeqPipe, level=OuterControl).name("x2253").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:39:11") } // Hwblock(Block(Const(())),false)
    val x2128_d0 = withCtrl(x2253) { Reg(init=Some(0)).name("x2128_d0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:43:20:acc") } // x2128 = RegNew(Const(0))
    isAccum(x2128_d0) = false
    bufferDepthOf(x2128_d0) = 1
    val x2128_d1 = withCtrl(x2253) { Reg(init=Some(0)).name("x2128_d1").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:43:20:acc") } // x2128 = RegNew(Const(0))
    isAccum(x2128_d1) = true
    bufferDepthOf(x2128_d1) = 1
    val x2129 = withCtrl(x2253) { ReadMem(x2113).name("x2129").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:44:19") } // RegRead(x2113)
    val x2130 = withCtrl(x2253) { Counter(min=Const(0), max=x2129, step=Const(65536), par=1).name("x2130").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:44:34") } // CounterNew(Const(0),x2129,Const(65536),Const(1))
    val x2131 = withCtrl(x2253) { CounterChain(List(x2130)).name("x2131").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // CounterChainNew(List(x2130))
    val x2249 = withCtrl(x2253) { LoopController(style=MetaPipe, level=OuterControl, cchain=x2131).name("x2249").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // UnrolledReduce(List(Const(true)),x2131,x2128,Block((x2128) => Const(())),List(List(b1269)),List(List(b1270)))
    val b1269 = withCtrl(x2249) { CounterIter(x2130, Some(0)).name("b1269") } // b1269
    val b1270 = withCtrl(x2249) { Const(true).name("b1270") } // b1270
    val x2132_d0_b0 = withCtrl(x2249) { SRAM(size=65536, banking=Strided(banks=16, stride=1)).name("x2132_d0_b0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:45:35:datesTile") } // x2132 = SRAMNew(ArrayBuffer(Const(65536)))
    isAccum(x2132_d0_b0) = false
    bufferDepthOf(x2132_d0_b0) = 2
    staticDimsOf(x2132_d0_b0) = List(65536)
    val x2133_d0_b0 = withCtrl(x2249) { SRAM(size=65536, banking=Strided(banks=16, stride=1)).name("x2133_d0_b0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:46:35:quantsTile") } // x2133 = SRAMNew(ArrayBuffer(Const(65536)))
    isAccum(x2133_d0_b0) = false
    bufferDepthOf(x2133_d0_b0) = 2
    staticDimsOf(x2133_d0_b0) = List(65536)
    val x2134_d0_b0 = withCtrl(x2249) { SRAM(size=65536, banking=Strided(banks=16, stride=1)).name("x2134_d0_b0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:47:33:disctsTile") } // x2134 = SRAMNew(ArrayBuffer(Const(65536)))
    isAccum(x2134_d0_b0) = false
    bufferDepthOf(x2134_d0_b0) = 2
    staticDimsOf(x2134_d0_b0) = List(65536)
    val x2135_d0_b0 = withCtrl(x2249) { SRAM(size=65536, banking=Strided(banks=16, stride=1)).name("x2135_d0_b0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:48:33:pricesTile") } // x2135 = SRAMNew(ArrayBuffer(Const(65536)))
    isAccum(x2135_d0_b0) = false
    bufferDepthOf(x2135_d0_b0) = 2
    staticDimsOf(x2135_d0_b0) = List(65536)
    val x2214 = withCtrl(x2249) { UnitController(style=ForkJoin, level=OuterControl).name("x2214").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:49:18") } // ParallelPipe(List(b1270),Block(Const(())))
    val x2137 = withCtrl(x2214) { UnitController(style=SeqPipe, level=InnerControl).name("x2137").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:49:18") } // UnitPipe(List(b1270),Block(Const(())))
    val x2136 = withCtrl(x2137) { OpDef(op=FixAdd, inputs=List(b1269, Const(65536))).name("x2136").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:37") } // FixAdd(b1269,Const(65536))
    val x2156 = withCtrl(x2214) { UnitController(style=StreamPipe, level=OuterControl).name("x2156").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // UnitPipe(List(b1270),Block(Const(())))
    val b2277 = withCtrl(x2156) { StreamOut(field="offset").name("b2277").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // x2138 = StreamOutNew(BurstCmdBus)
    isAccum(b2277) = false
    bufferDepthOf(b2277) = 1
    val b2278 = withCtrl(x2156) { StreamOut(field="size").name("b2278").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // x2138 = StreamOutNew(BurstCmdBus)
    isAccum(b2278) = false
    bufferDepthOf(b2278) = 1
    val x2139 = withCtrl(x2156) { StreamIn(field="data").name("x2139").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // x2139 = StreamInNew(BurstDataBus())
    isAccum(x2139) = false
    bufferDepthOf(x2139) = 1
    val x2147 = withCtrl(x2156) { UnitController(style=SeqPipe, level=InnerControl).name("x2147").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // UnitPipe(List(b1270),Block(x2146))
    val x2140 = withCtrl(x2147) { b1269 } // FixConvert(b1269,TRUE,_32,_0) (Same Type. No op)
    val x2141 = withCtrl(x2147) { OpDef(op=FixSla, inputs=List(x2140, Const(2))).name("x2141").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // FixLsh(x2140,Const(2))
    val x2142 = withCtrl(x2147) { x2141 } // FixConvert(x2141,TRUE,_64,_0)
    val x2143 = withCtrl(x2147) { DramAddress(x2116).name("x2143").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // GetDRAMAddress(x2116)
    val x2145_x2144 = withCtrl(x2147) { OpDef(op=FixAdd, inputs=List(x2142, x2143)).name("x2145_x2144").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // FixAdd(x2142,x2143)
    // x2145 = SimpleStruct(ArrayBuffer((offset,x2144), (size,Const(262144)), (isLoad,Const(true))))
    val x2146_b2279_b2277 = withCtrl(x2147) { WriteMem(b2277, x2145_x2144).name("x2146_b2279_b2277").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // StreamWrite(x2138,x2145,b1270)
    val x2146_b2280_b2278 = withCtrl(x2147) { WriteMem(b2278, Const(262144)).name("x2146_b2280_b2278").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // StreamWrite(x2138,x2145,b1270)
    val x2148 = withCtrl(x2156) { FringeDenseLoad(dram=List(x2116), cmdStream=List(b2277, b2278), dataStream=List(x2139)).name("x2148").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // FringeDenseLoad(x2116,x2138,x2139)
    val x2149 = withCtrl(x2156) { Counter(min=Const(0), max=Const(65536), step=Const(1), par=16).name("x2149").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // CounterNew(Const(0),Const(65536),Const(1),Const(16))
    val x2150 = withCtrl(x2156) { CounterChain(List(x2149)).name("x2150").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // CounterChainNew(List(x2149))
    val x2155 = withCtrl(x2156) { LoopController(style=InnerPipe, level=InnerControl, cchain=x2150).name("x2155").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // UnrolledForeach(List(b1270),x2150,Block(Const(())),List(List(b1290)),List(List(b1291)))
    val b1290 = withCtrl(x2155) { CounterIter(x2149, None).name("b1290") } // b1290
    val b1291 = withCtrl(x2155) { Const(true).name("b1291") } // b1291
    val x2151 = withCtrl(x2155) { OpDef(op=BitAnd, inputs=List(b1291, b1270)).name("x2151").srcCtx("UnrollingBase.scala:28:66") } // And(b1291,b1270)
    val x2152_x2152 = withCtrl(x2155) { ReadMem(x2139).name("x2152_x2152").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // ParStreamRead(x2139,List(x2151))
    val x2153_x2153 = withCtrl(x2155) { x2152_x2152 } // VectorApply(x2152,0)
    val x2154 = withCtrl(x2155) { StoreBanks(List(List(x2132_d0_b0)), List(b1290), x2153_x2153).name("x2154").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:50:22") } // ParSRAMStore(x2132,List(List(b1290)),List(x2153),List(x2151))
    val x2175 = withCtrl(x2214) { UnitController(style=StreamPipe, level=OuterControl).name("x2175").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // UnitPipe(List(b1270),Block(Const(())))
    val b2281 = withCtrl(x2175) { StreamOut(field="offset").name("b2281").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // x2157 = StreamOutNew(BurstCmdBus)
    isAccum(b2281) = false
    bufferDepthOf(b2281) = 1
    val b2282 = withCtrl(x2175) { StreamOut(field="size").name("b2282").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // x2157 = StreamOutNew(BurstCmdBus)
    isAccum(b2282) = false
    bufferDepthOf(b2282) = 1
    val x2158 = withCtrl(x2175) { StreamIn(field="data").name("x2158").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // x2158 = StreamInNew(BurstDataBus())
    isAccum(x2158) = false
    bufferDepthOf(x2158) = 1
    val x2166 = withCtrl(x2175) { UnitController(style=SeqPipe, level=InnerControl).name("x2166").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // UnitPipe(List(b1270),Block(x2165))
    val x2159 = withCtrl(x2166) { b1269 } // FixConvert(b1269,TRUE,_32,_0) (Same Type. No op)
    val x2160 = withCtrl(x2166) { OpDef(op=FixSla, inputs=List(x2159, Const(2))).name("x2160").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // FixLsh(x2159,Const(2))
    val x2161 = withCtrl(x2166) { x2160 } // FixConvert(x2160,TRUE,_64,_0)
    val x2162 = withCtrl(x2166) { DramAddress(x2118).name("x2162").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // GetDRAMAddress(x2118)
    val x2164_x2163 = withCtrl(x2166) { OpDef(op=FixAdd, inputs=List(x2161, x2162)).name("x2164_x2163").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // FixAdd(x2161,x2162)
    // x2164 = SimpleStruct(ArrayBuffer((offset,x2163), (size,Const(262144)), (isLoad,Const(true))))
    val x2165_b2283_b2281 = withCtrl(x2166) { WriteMem(b2281, x2164_x2163).name("x2165_b2283_b2281").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // StreamWrite(x2157,x2164,b1270)
    val x2165_b2284_b2282 = withCtrl(x2166) { WriteMem(b2282, Const(262144)).name("x2165_b2284_b2282").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // StreamWrite(x2157,x2164,b1270)
    val x2167 = withCtrl(x2175) { FringeDenseLoad(dram=List(x2118), cmdStream=List(b2281, b2282), dataStream=List(x2158)).name("x2167").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // FringeDenseLoad(x2118,x2157,x2158)
    val x2168 = withCtrl(x2175) { Counter(min=Const(0), max=Const(65536), step=Const(1), par=16).name("x2168").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // CounterNew(Const(0),Const(65536),Const(1),Const(16))
    val x2169 = withCtrl(x2175) { CounterChain(List(x2168)).name("x2169").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // CounterChainNew(List(x2168))
    val x2174 = withCtrl(x2175) { LoopController(style=InnerPipe, level=InnerControl, cchain=x2169).name("x2174").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // UnrolledForeach(List(b1270),x2169,Block(Const(())),List(List(b1311)),List(List(b1312)))
    val b1311 = withCtrl(x2174) { CounterIter(x2168, None).name("b1311") } // b1311
    val b1312 = withCtrl(x2174) { Const(true).name("b1312") } // b1312
    val x2170 = withCtrl(x2174) { OpDef(op=BitAnd, inputs=List(b1312, b1270)).name("x2170").srcCtx("UnrollingBase.scala:28:66") } // And(b1312,b1270)
    val x2171_x2171 = withCtrl(x2174) { ReadMem(x2158).name("x2171_x2171").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // ParStreamRead(x2158,List(x2170))
    val x2172_x2172 = withCtrl(x2174) { x2171_x2171 } // VectorApply(x2171,0)
    val x2173 = withCtrl(x2174) { StoreBanks(List(List(x2133_d0_b0)), List(b1311), x2172_x2172).name("x2173").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:51:22") } // ParSRAMStore(x2133,List(List(b1311)),List(x2172),List(x2170))
    val x2194 = withCtrl(x2214) { UnitController(style=StreamPipe, level=OuterControl).name("x2194").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // UnitPipe(List(b1270),Block(Const(())))
    val b2285 = withCtrl(x2194) { StreamOut(field="offset").name("b2285").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // x2176 = StreamOutNew(BurstCmdBus)
    isAccum(b2285) = false
    bufferDepthOf(b2285) = 1
    val b2286 = withCtrl(x2194) { StreamOut(field="size").name("b2286").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // x2176 = StreamOutNew(BurstCmdBus)
    isAccum(b2286) = false
    bufferDepthOf(b2286) = 1
    val x2177 = withCtrl(x2194) { StreamIn(field="data").name("x2177").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // x2177 = StreamInNew(BurstDataBus())
    isAccum(x2177) = false
    bufferDepthOf(x2177) = 1
    val x2185 = withCtrl(x2194) { UnitController(style=SeqPipe, level=InnerControl).name("x2185").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // UnitPipe(List(b1270),Block(x2184))
    val x2178 = withCtrl(x2185) { b1269 } // FixConvert(b1269,TRUE,_32,_0) (Same Type. No op)
    val x2179 = withCtrl(x2185) { OpDef(op=FixSla, inputs=List(x2178, Const(2))).name("x2179").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // FixLsh(x2178,Const(2))
    val x2180 = withCtrl(x2185) { x2179 } // FixConvert(x2179,TRUE,_64,_0)
    val x2181 = withCtrl(x2185) { DramAddress(x2120).name("x2181").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // GetDRAMAddress(x2120)
    val x2183_x2182 = withCtrl(x2185) { OpDef(op=FixAdd, inputs=List(x2180, x2181)).name("x2183_x2182").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // FixAdd(x2180,x2181)
    // x2183 = SimpleStruct(ArrayBuffer((offset,x2182), (size,Const(262144)), (isLoad,Const(true))))
    val x2184_b2287_b2285 = withCtrl(x2185) { WriteMem(b2285, x2183_x2182).name("x2184_b2287_b2285").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // StreamWrite(x2176,x2183,b1270)
    val x2184_b2288_b2286 = withCtrl(x2185) { WriteMem(b2286, Const(262144)).name("x2184_b2288_b2286").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // StreamWrite(x2176,x2183,b1270)
    val x2186 = withCtrl(x2194) { FringeDenseLoad(dram=List(x2120), cmdStream=List(b2285, b2286), dataStream=List(x2177)).name("x2186").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // FringeDenseLoad(x2120,x2176,x2177)
    val x2187 = withCtrl(x2194) { Counter(min=Const(0), max=Const(65536), step=Const(1), par=16).name("x2187").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // CounterNew(Const(0),Const(65536),Const(1),Const(16))
    val x2188 = withCtrl(x2194) { CounterChain(List(x2187)).name("x2188").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // CounterChainNew(List(x2187))
    val x2193 = withCtrl(x2194) { LoopController(style=InnerPipe, level=InnerControl, cchain=x2188).name("x2193").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // UnrolledForeach(List(b1270),x2188,Block(Const(())),List(List(b1332)),List(List(b1333)))
    val b1332 = withCtrl(x2193) { CounterIter(x2187, None).name("b1332") } // b1332
    val b1333 = withCtrl(x2193) { Const(true).name("b1333") } // b1333
    val x2189 = withCtrl(x2193) { OpDef(op=BitAnd, inputs=List(b1333, b1270)).name("x2189").srcCtx("UnrollingBase.scala:28:66") } // And(b1333,b1270)
    val x2190_x2190 = withCtrl(x2193) { ReadMem(x2177).name("x2190_x2190").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // ParStreamRead(x2177,List(x2189))
    val x2191_x2191 = withCtrl(x2193) { x2190_x2190 } // VectorApply(x2190,0)
    val x2192 = withCtrl(x2193) { StoreBanks(List(List(x2134_d0_b0)), List(b1332), x2191_x2191).name("x2192").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:52:22") } // ParSRAMStore(x2134,List(List(b1332)),List(x2191),List(x2189))
    val x2213 = withCtrl(x2214) { UnitController(style=StreamPipe, level=OuterControl).name("x2213").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // UnitPipe(List(b1270),Block(Const(())))
    val b2289 = withCtrl(x2213) { StreamOut(field="offset").name("b2289").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // x2195 = StreamOutNew(BurstCmdBus)
    isAccum(b2289) = false
    bufferDepthOf(b2289) = 1
    val b2290 = withCtrl(x2213) { StreamOut(field="size").name("b2290").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // x2195 = StreamOutNew(BurstCmdBus)
    isAccum(b2290) = false
    bufferDepthOf(b2290) = 1
    val x2196 = withCtrl(x2213) { StreamIn(field="data").name("x2196").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // x2196 = StreamInNew(BurstDataBus())
    isAccum(x2196) = false
    bufferDepthOf(x2196) = 1
    val x2204 = withCtrl(x2213) { UnitController(style=SeqPipe, level=InnerControl).name("x2204").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // UnitPipe(List(b1270),Block(x2203))
    val x2197 = withCtrl(x2204) { b1269 } // FixConvert(b1269,TRUE,_32,_0) (Same Type. No op)
    val x2198 = withCtrl(x2204) { OpDef(op=FixSla, inputs=List(x2197, Const(2))).name("x2198").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // FixLsh(x2197,Const(2))
    val x2199 = withCtrl(x2204) { x2198 } // FixConvert(x2198,TRUE,_64,_0)
    val x2200 = withCtrl(x2204) { DramAddress(x2122).name("x2200").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // GetDRAMAddress(x2122)
    val x2202_x2201 = withCtrl(x2204) { OpDef(op=FixAdd, inputs=List(x2199, x2200)).name("x2202_x2201").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // FixAdd(x2199,x2200)
    // x2202 = SimpleStruct(ArrayBuffer((offset,x2201), (size,Const(262144)), (isLoad,Const(true))))
    val x2203_b2291_b2289 = withCtrl(x2204) { WriteMem(b2289, x2202_x2201).name("x2203_b2291_b2289").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // StreamWrite(x2195,x2202,b1270)
    val x2203_b2292_b2290 = withCtrl(x2204) { WriteMem(b2290, Const(262144)).name("x2203_b2292_b2290").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // StreamWrite(x2195,x2202,b1270)
    val x2205 = withCtrl(x2213) { FringeDenseLoad(dram=List(x2122), cmdStream=List(b2289, b2290), dataStream=List(x2196)).name("x2205").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // FringeDenseLoad(x2122,x2195,x2196)
    val x2206 = withCtrl(x2213) { Counter(min=Const(0), max=Const(65536), step=Const(1), par=16).name("x2206").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // CounterNew(Const(0),Const(65536),Const(1),Const(16))
    val x2207 = withCtrl(x2213) { CounterChain(List(x2206)).name("x2207").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // CounterChainNew(List(x2206))
    val x2212 = withCtrl(x2213) { LoopController(style=InnerPipe, level=InnerControl, cchain=x2207).name("x2212").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // UnrolledForeach(List(b1270),x2207,Block(Const(())),List(List(b1353)),List(List(b1354)))
    val b1353 = withCtrl(x2212) { CounterIter(x2206, None).name("b1353") } // b1353
    val b1354 = withCtrl(x2212) { Const(true).name("b1354") } // b1354
    val x2208 = withCtrl(x2212) { OpDef(op=BitAnd, inputs=List(b1354, b1270)).name("x2208").srcCtx("UnrollingBase.scala:28:66") } // And(b1354,b1270)
    val x2209_x2209 = withCtrl(x2212) { ReadMem(x2196).name("x2209_x2209").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // ParStreamRead(x2196,List(x2208))
    val x2210_x2210 = withCtrl(x2212) { x2209_x2209 } // VectorApply(x2209,0)
    val x2211 = withCtrl(x2212) { StoreBanks(List(List(x2135_d0_b0)), List(b1353), x2210_x2210).name("x2211").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:53:22") } // ParSRAMStore(x2135,List(List(b1353)),List(x2210),List(x2208))
    val x2215_d0 = withCtrl(x2249) { Reg(init=Some(0)).name("x2215_d0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:55:19") } // x2215 = RegNew(Const(0))
    isAccum(x2215_d0) = false
    bufferDepthOf(x2215_d0) = 2
    val x2215_d1 = withCtrl(x2249) { Reg(init=Some(0)).name("x2215_d1").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:55:19") } // x2215 = RegNew(Const(0))
    isAccum(x2215_d1) = true
    bufferDepthOf(x2215_d1) = 1
    val x2216 = withCtrl(x2249) { Counter(min=Const(0), max=Const(65536), step=Const(1), par=16).name("x2216").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:55:27") } // CounterNew(Const(0),Const(65536),Const(1),Const(16))
    val x2217 = withCtrl(x2249) { CounterChain(List(x2216)).name("x2217").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // CounterChainNew(List(x2216))
    val x2242 = withCtrl(x2249) { LoopController(style=InnerPipe, level=InnerControl, cchain=x2217).name("x2242").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // UnrolledReduce(List(b1270),x2217,x2215,Block((x2215) => Const(())),List(List(b1365)),List(List(b1366)))
    val b1365 = withCtrl(x2242) { CounterIter(x2216, None).name("b1365") } // b1365
    val b1366 = withCtrl(x2242) { Const(true).name("b1366") } // b1366
    val x2218 = withCtrl(x2242) { OpDef(op=BitAnd, inputs=List(b1366, b1270)).name("x2218").srcCtx("UnrollingBase.scala:28:66") } // And(b1366,b1270)
    val x2219 = withCtrl(x2242) { LoadBanks(List(x2132_d0_b0), List(b1365)).name("x2219").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:56:32:date") } // ParSRAMLoad(x2132,List(List(b1365)),List(x2218))
    val x2220 = withCtrl(x2242) { x2219 } // VectorApply(x2219,0)
    val x2221 = withCtrl(x2242) { LoadBanks(List(x2134_d0_b0), List(b1365)).name("x2221").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:57:33:disct") } // ParSRAMLoad(x2134,List(List(b1365)),List(x2218))
    val x2222 = withCtrl(x2242) { x2221 } // VectorApply(x2221,0)
    val x2223 = withCtrl(x2242) { LoadBanks(List(x2133_d0_b0), List(b1365)).name("x2223").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:58:33:quant") } // ParSRAMLoad(x2133,List(List(b1365)),List(x2218))
    val x2224 = withCtrl(x2242) { x2223 } // VectorApply(x2223,0)
    val x2225 = withCtrl(x2242) { LoadBanks(List(x2135_d0_b0), List(b1365)).name("x2225").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:59:33:price") } // ParSRAMLoad(x2135,List(List(b1365)),List(x2218))
    val x2226 = withCtrl(x2242) { x2225 } // VectorApply(x2225,0)
    val x2227 = withCtrl(x2242) { OpDef(op=FixLt, inputs=List(Const(0), x2220)).name("x2227").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:28") } // FixLt(Const(0),x2220)
    val x2228 = withCtrl(x2242) { OpDef(op=FixLt, inputs=List(x2220, Const(9999))).name("x2228").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:46") } // FixLt(x2220,Const(9999))
    val x2229 = withCtrl(x2242) { OpDef(op=BitAnd, inputs=List(x2227, x2228)).name("x2229").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:38") } // And(x2227,x2228)
    val x2230 = withCtrl(x2242) { OpDef(op=FixLeq, inputs=List(Const(0), x2222)).name("x2230").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:65") } // FixLeq(Const(0),x2222)
    val x2231 = withCtrl(x2242) { OpDef(op=BitAnd, inputs=List(x2229, x2230)).name("x2231").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:56") } // And(x2229,x2230)
    val x2232 = withCtrl(x2242) { OpDef(op=FixLeq, inputs=List(x2222, Const(9999))).name("x2232").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:92") } // FixLeq(x2222,Const(9999))
    val x2233 = withCtrl(x2242) { OpDef(op=BitAnd, inputs=List(x2231, x2232)).name("x2233").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:83") } // And(x2231,x2232)
    val x2234 = withCtrl(x2242) { OpDef(op=FixLt, inputs=List(x2224, Const(24))).name("x2234").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:119") } // FixLt(x2224,Const(24))
    val x2235 = withCtrl(x2242) { OpDef(op=BitAnd, inputs=List(x2233, x2234)).name("x2235").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:60:110:valid") } // And(x2233,x2234)
    val x2236 = withCtrl(x2242) { OpDef(op=FixMul, inputs=List(x2226, x2222)).name("x2236").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:61:28") } // FixMul(x2226,x2222)
    val x2237 = withCtrl(x2242) { OpDef(op=MuxOp, inputs=List(x2235, x2236, Const(0))).name("x2237").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:61:14") } // Mux(x2235,x2236,Const(0))
    val x2238 = withCtrl(x2242) { ReadMem(x2215_d1).name("x2238").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // RegRead(x2215)
    val x2239 = withCtrl(x2242) { OpDef(op=FixEql, inputs=List(b1365, Const(0))).name("x2239").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // FixEql(b1365,Const(0))
    val x2240 = withCtrl(x2242) { ReduceAccumOp(op=FixAdd, input=x2237, accum=x2238).name("x2240").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:12") } // FixAdd(x2237,x2238)
    val x2241_x2215_d0 = withCtrl(x2242) { WriteMem(x2215_d0, x2240).name("x2241_x2215_d0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // RegWrite(x2215,x2240,b1270)
    antiDepsOf(x2241_x2215_d0)=List(x2238)
    val x2241_x2215_d1 = withCtrl(x2242) { WriteMem(x2215_d1, x2240).name("x2241_x2215_d1").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // RegWrite(x2215,x2240,b1270)
    antiDepsOf(x2241_x2215_d1)=List(x2238)
    val x2248 = withCtrl(x2249) { UnitController(style=SeqPipe, level=InnerControl).name("x2248").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // UnitPipe(List(Const(true)),Block(x2247))
    val x2243 = withCtrl(x2248) { ReadMem(x2128_d1).name("x2243").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // RegRead(x2128)
    val x2244 = withCtrl(x2248) { OpDef(op=FixEql, inputs=List(b1269, Const(0))).name("x2244").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // FixEql(b1269,Const(0))
    val x2245 = withCtrl(x2248) { ReadMem(x2215_d0).name("x2245").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:62:10") } // RegRead(x2215)
    val x2246 = withCtrl(x2248) { OpDef(op=FixAdd, inputs=List(x2245, x2243)).name("x2246").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:10") } // FixAdd(x2245,x2243)
    val x2247_x2128_d0 = withCtrl(x2248) { WriteMem(x2128_d0, x2246).name("x2247_x2128_d0").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // RegWrite(x2128,x2246,Const(true))
    antiDepsOf(x2247_x2128_d0)=List(x2243)
    val x2247_x2128_d1 = withCtrl(x2248) { WriteMem(x2128_d1, x2246).name("x2247_x2128_d1").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:63:8") } // RegWrite(x2128,x2246,Const(true))
    antiDepsOf(x2247_x2128_d1)=List(x2243)
    val x2252 = withCtrl(x2253) { UnitController(style=SeqPipe, level=InnerControl).name("x2252").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:39:11") } // UnitPipe(List(Const(true)),Block(Const(())))
    val x2250 = withCtrl(x2252) { ReadMem(x2128_d0).name("x2250").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:65:14") } // RegRead(x2128)
    val x2251_x2123 = withCtrl(x2252) { WriteMem(x2123, x2250).name("x2251_x2123").srcCtx("TPCHQ6__N_1048576_ts_65536_op_1.scala:65:11") } // RegWrite(x2123,x2250,Const(true))
    
  }
}