import pir._
import pir.node._
import arch._
import prism.enums._

object GDA extends PIRApp {
  def main(implicit design:PIRDesign) = {
    import design.pirmeta._
    val x3321 = top.argFringe.argIn(init=0).name("x3321").ctrl(top) // ArgInNew(Const(0))
    boundOf(x3321) = 1024
    val x3323 = ReadMem(x3321).name("x3323").ctrl(top) // RegRead(x3321)
    val x3324 = DRAM().name("x3324").ctrl(top) // x3324 = DRAMNew(ArrayBuffer(x3323, Const(96)),Const(0))
    val x3325 = ReadMem(x3321).name("x3325").ctrl(top) // RegRead(x3321)
    val x3326 = DRAM().name("x3326").ctrl(top) // x3326 = DRAMNew(ArrayBuffer(x3325),Const(0))
    val x3327 = DRAM().name("x3327").ctrl(top) // x3327 = DRAMNew(ArrayBuffer(Const(96)),Const(0))
    val x3328 = DRAM().name("x3328").ctrl(top) // x3328 = DRAMNew(ArrayBuffer(Const(96)),Const(0))
    val x3329 = DRAM().name("x3329").ctrl(top) // x3329 = DRAMNew(ArrayBuffer(Const(96), Const(96)),Const(0))
    val x3522 = UnitController(style=SeqPipe, level=OuterControl).name("x3522").ctrl(top) // Hwblock(Block(Const(())),false)
    val x3334_d0_b0 = SRAM(size=6, banking=Strided(banks=16, stride=1)).name("x3334_d0_b0").ctrl(x3522) // x3334 = SRAMNew(ArrayBuffer(Const(96)))
    isAccum(x3334_d0_b0) = false
    bufferDepthOf(x3334_d0_b0) = 1
    val x3335_d0_b0 = SRAM(size=6, banking=Strided(banks=16, stride=1)).name("x3335_d0_b0").ctrl(x3522) // x3335 = SRAMNew(ArrayBuffer(Const(96)))
    isAccum(x3335_d0_b0) = false
    bufferDepthOf(x3335_d0_b0) = 1
    val x3354 = UnitController(style=StreamPipe, level=OuterControl).name("x3354").ctrl(x3522) // UnitPipe(List(Const(true)),Block(Const(())))
    val b3574 = StreamOut(field="offset").name("b3574").ctrl(x3354) // x3336 = StreamOutNew(BurstCmdBus)
    val b3575 = StreamOut(field="size").name("b3575").ctrl(x3354) // x3336 = StreamOutNew(BurstCmdBus)
    val x3337 = StreamIn(field="data").name("x3337").ctrl(x3354) // x3337 = StreamInNew(BurstDataBus())
    val x3346 = UnitController(style=SeqPipe, level=InnerControl).name("x3346").ctrl(x3354) // UnitPipe(List(Const(true)),Block(x3345))
    val x3338 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x3339 = OpDef(op=FixSla, inputs=List(x3338, Const(2))).name("x3339").ctrl(x3346) // FixLsh(x3338,Const(2))
    val x3340 = x3339 // FixConvert(x3339,TRUE,_64,_0)
    val x3341 = top.argFringe.dramAddress(x3327).name("x3341").ctrl(x3346) // GetDRAMAddress(x3327)
    val x3342 = OpDef(op=FixAdd, inputs=List(x3340, x3341)).name("x3342").ctrl(x3346) // FixAdd(x3340,x3341)
    val x3344_x3343 = x3342 // FixConvert(x3342,TRUE,_64,_0)
    // x3344 = SimpleStruct(ArrayBuffer((offset,x3343), (size,Const(384)), (isLoad,Const(true))))
    val b3576_b3574 = WriteMem(b3574, x3344_x3343).name("b3576_b3574").ctrl(x3346) // StreamWrite(x3336,x3344,Const(true))
    val b3577_b3575 = WriteMem(b3575, Const(384)).name("b3577_b3575").ctrl(x3346) // StreamWrite(x3336,x3344,Const(true))
    val x3347 = FringeContainer(x3327,b3574,b3575,x3337).name("x3347").ctrl(x3354) // FringeDenseLoad(x3327,x3336,x3337)
    val x3348 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3348").ctrl(x3354) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3349 = CounterChain(List(x3348)).name("x3349").ctrl(x3354) // CounterChainNew(List(x3348))
    val x3353 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3349).name("x3353").ctrl(x3354) // UnrolledForeach(List(Const(true)),x3349,Block(Const(())),List(List(b2022)),List(List(b2023)))
    val b2022 = CounterIter(x3348, None).ctrl(x3353).name("b2022")
    val b2023 = DummyOp().ctrl(x3353).name("b2023")
    val x3350_x3350 = ReadMem(x3337).name("x3350").ctrl(x3353) // ParStreamRead(x3337,List(b2023))
    val x3351_x3351 = x3350_x3350 // x3351 = VectorApply(x3350,0)
    val x3352 = StoreBanks(List(x3334_d0_b0), List(b2022), x3351_x3351).name("x3352").ctrl(x3353) // ParSRAMStore(x3334,List(List(b2022)),List(x3351),List(b2023))
    val x3373 = UnitController(style=StreamPipe, level=OuterControl).name("x3373").ctrl(x3522) // UnitPipe(List(Const(true)),Block(Const(())))
    val b3578 = StreamOut(field="offset").name("b3578").ctrl(x3373) // x3355 = StreamOutNew(BurstCmdBus)
    val b3579 = StreamOut(field="size").name("b3579").ctrl(x3373) // x3355 = StreamOutNew(BurstCmdBus)
    val x3356 = StreamIn(field="data").name("x3356").ctrl(x3373) // x3356 = StreamInNew(BurstDataBus())
    val x3365 = UnitController(style=SeqPipe, level=InnerControl).name("x3365").ctrl(x3373) // UnitPipe(List(Const(true)),Block(x3364))
    val x3357 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x3358 = OpDef(op=FixSla, inputs=List(x3357, Const(2))).name("x3358").ctrl(x3365) // FixLsh(x3357,Const(2))
    val x3359 = x3358 // FixConvert(x3358,TRUE,_64,_0)
    val x3360 = top.argFringe.dramAddress(x3328).name("x3360").ctrl(x3365) // GetDRAMAddress(x3328)
    val x3361 = OpDef(op=FixAdd, inputs=List(x3359, x3360)).name("x3361").ctrl(x3365) // FixAdd(x3359,x3360)
    val x3363_x3362 = x3361 // FixConvert(x3361,TRUE,_64,_0)
    // x3363 = SimpleStruct(ArrayBuffer((offset,x3362), (size,Const(384)), (isLoad,Const(true))))
    val b3580_b3578 = WriteMem(b3578, x3363_x3362).name("b3580_b3578").ctrl(x3365) // StreamWrite(x3355,x3363,Const(true))
    val b3581_b3579 = WriteMem(b3579, Const(384)).name("b3581_b3579").ctrl(x3365) // StreamWrite(x3355,x3363,Const(true))
    val x3366 = FringeContainer(x3328,b3578,b3579,x3356).name("x3366").ctrl(x3373) // FringeDenseLoad(x3328,x3355,x3356)
    val x3367 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3367").ctrl(x3373) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3368 = CounterChain(List(x3367)).name("x3368").ctrl(x3373) // CounterChainNew(List(x3367))
    val x3372 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3368).name("x3372").ctrl(x3373) // UnrolledForeach(List(Const(true)),x3368,Block(Const(())),List(List(b2043)),List(List(b2044)))
    val b2043 = CounterIter(x3367, None).ctrl(x3372).name("b2043")
    val b2044 = DummyOp().ctrl(x3372).name("b2044")
    val x3369_x3369 = ReadMem(x3356).name("x3369").ctrl(x3372) // ParStreamRead(x3356,List(b2044))
    val x3370_x3370 = x3369_x3369 // x3370 = VectorApply(x3369,0)
    val x3371 = StoreBanks(List(x3335_d0_b0), List(b2043), x3370_x3370).name("x3371").ctrl(x3372) // ParSRAMStore(x3335,List(List(b2043)),List(x3370),List(b2044))
    val x3374_d0_b0 = SRAM(size=576, banking=Strided(banks=16, stride=1)).name("x3374_d0_b0").ctrl(x3522) // x3374 = SRAMNew(ArrayBuffer(Const(96), Const(96)))
    isAccum(x3374_d0_b0) = false
    bufferDepthOf(x3374_d0_b0) = 1
    val x3374_d1_b0 = SRAM(size=9216, banking=NoBanking()).name("x3374_d1_b0").ctrl(x3522) // x3374 = SRAMNew(ArrayBuffer(Const(96), Const(96)))
    isAccum(x3374_d1_b0) = true
    bufferDepthOf(x3374_d1_b0) = 1
    val x3375 = ReadMem(x3321).name("x3375").ctrl(x3522) // RegRead(x3321)
    val x3376 = Counter(min=Const(0), max=x3375, step=Const(32), par=1).name("x3376").ctrl(x3522) // CounterNew(Const(0),x3375,Const(32),Const(1))
    val x3377 = CounterChain(List(x3376)).name("x3377").ctrl(x3522) // CounterChainNew(List(x3376))
    val x3492 = LoopController(style=MetaPipe, level=OuterControl, cchain=x3377).name("x3492").ctrl(x3522) // UnrolledReduce(List(Const(true)),x3377,x3374,Block((x3374) => Const(())),List(List(b2058)),List(List(b2059)))
    val b2058 = CounterIter(x3376, Some(0)).ctrl(x3492).name("b2058")
    val b2059 = DummyOp().ctrl(x3492).name("b2059")
    val x3378_d0_b0 = SRAM(size=2, banking=Strided(banks=16, stride=1)).name("x3378_d0_b0").ctrl(x3492) // x3378 = SRAMNew(ArrayBuffer(Const(32)))
    isAccum(x3378_d0_b0) = false
    bufferDepthOf(x3378_d0_b0) = 2
    val x3379_d0_b0 = SRAM(size=192, banking=Strided(banks=16, stride=1)).name("x3379_d0_b0").ctrl(x3492) // x3379 = SRAMNew(ArrayBuffer(Const(32), Const(96)))
    isAccum(x3379_d0_b0) = false
    bufferDepthOf(x3379_d0_b0) = 2
    val x3381 = UnitController(style=SeqPipe, level=InnerControl).name("x3381").ctrl(x3492) // UnitPipe(List(b2059),Block(Const(())))
    val x3380 = OpDef(op=FixAdd, inputs=List(b2058, Const(32))).name("x3380").ctrl(x3381) // FixAdd(b2058,Const(32))
    val x3401 = UnitController(style=StreamPipe, level=OuterControl).name("x3401").ctrl(x3492) // UnitPipe(List(b2059),Block(Const(())))
    val b3582 = StreamOut(field="offset").name("b3582").ctrl(x3401) // x3382 = StreamOutNew(BurstCmdBus)
    val b3583 = StreamOut(field="size").name("b3583").ctrl(x3401) // x3382 = StreamOutNew(BurstCmdBus)
    val x3383 = StreamIn(field="data").name("x3383").ctrl(x3401) // x3383 = StreamInNew(BurstDataBus())
    val x3392 = UnitController(style=SeqPipe, level=InnerControl).name("x3392").ctrl(x3401) // UnitPipe(List(b2059),Block(x3391))
    val x3384 = b2058 // FixConvert(b2058,TRUE,_32,_0)
    val x3385 = OpDef(op=FixSla, inputs=List(x3384, Const(2))).name("x3385").ctrl(x3392) // FixLsh(x3384,Const(2))
    val x3386 = x3385 // FixConvert(x3385,TRUE,_64,_0)
    val x3387 = top.argFringe.dramAddress(x3326).name("x3387").ctrl(x3392) // GetDRAMAddress(x3326)
    val x3388 = OpDef(op=FixAdd, inputs=List(x3386, x3387)).name("x3388").ctrl(x3392) // FixAdd(x3386,x3387)
    val x3390_x3389 = x3388 // FixConvert(x3388,TRUE,_64,_0)
    // x3390 = SimpleStruct(ArrayBuffer((offset,x3389), (size,Const(128)), (isLoad,Const(true))))
    val b3584_b3582 = WriteMem(b3582, x3390_x3389).name("b3584_b3582").ctrl(x3392) // StreamWrite(x3382,x3390,b2059)
    val b3585_b3583 = WriteMem(b3583, Const(128)).name("b3585_b3583").ctrl(x3392) // StreamWrite(x3382,x3390,b2059)
    val x3393 = FringeContainer(x3326,b3582,b3583,x3383).name("x3393").ctrl(x3401) // FringeDenseLoad(x3326,x3382,x3383)
    val x3394 = Counter(min=Const(0), max=Const(32), step=Const(1), par=16).name("x3394").ctrl(x3401) // CounterNew(Const(0),Const(32),Const(1),Const(16))
    val x3395 = CounterChain(List(x3394)).name("x3395").ctrl(x3401) // CounterChainNew(List(x3394))
    val x3400 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3395).name("x3400").ctrl(x3401) // UnrolledForeach(List(b2059),x3395,Block(Const(())),List(List(b2078)),List(List(b2079)))
    val b2078 = CounterIter(x3394, None).ctrl(x3400).name("b2078")
    val b2079 = DummyOp().ctrl(x3400).name("b2079")
    val x3396 = OpDef(op=BitAnd, inputs=List(b2079, b2059)).name("x3396").ctrl(x3400) // And(b2079,b2059)
    val x3397_x3397 = ReadMem(x3383).name("x3397").ctrl(x3400) // ParStreamRead(x3383,List(x3396))
    val x3398_x3398 = x3397_x3397 // x3398 = VectorApply(x3397,0)
    val x3399 = StoreBanks(List(x3378_d0_b0), List(b2078), x3398_x3398).name("x3399").ctrl(x3400) // ParSRAMStore(x3378,List(List(b2078)),List(x3398),List(x3396))
    val x3402 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3402").ctrl(x3492) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3403 = CounterChain(List(x3402)).name("x3403").ctrl(x3492) // CounterChainNew(List(x3402))
    val x3429 = LoopController(style=StreamPipe, level=OuterControl, cchain=x3403).name("x3429").ctrl(x3492) // UnrolledForeach(List(b2059),x3403,Block(Const(())),List(List(b2088)),List(List(b2089)))
    val b2088 = CounterIter(x3402, Some(0)).ctrl(x3429).name("b2088")
    val b2089 = DummyOp().ctrl(x3429).name("b2089")
    val b3586 = StreamOut(field="offset").name("b3586").ctrl(x3429) // x3404 = StreamOutNew(BurstCmdBus)
    val b3587 = StreamOut(field="size").name("b3587").ctrl(x3429) // x3404 = StreamOutNew(BurstCmdBus)
    val x3405 = StreamIn(field="data").name("x3405").ctrl(x3429) // x3405 = StreamInNew(BurstDataBus())
    val x3419 = UnitController(style=SeqPipe, level=InnerControl).name("x3419").ctrl(x3429) // UnitPipe(List(b2089, b2059),Block(x3418))
    val x3406 = OpDef(op=FixAdd, inputs=List(b2058, b2088)).name("x3406").ctrl(x3419) // FixAdd(b2058,b2088)
    val x3407 = x3406 // FixConvert(x3406,TRUE,_32,_0)
    val x3408 = OpDef(op=FixMul, inputs=List(x3407, Const(96))).name("x3408").ctrl(x3419) // FixMul(x3407,Const(96))
    val x3409 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x3410 = OpDef(op=FixAdd, inputs=List(x3408, x3409)).name("x3410").ctrl(x3419) // FixAdd(x3408,x3409)
    val x3411 = OpDef(op=FixSla, inputs=List(x3410, Const(2))).name("x3411").ctrl(x3419) // FixLsh(x3410,Const(2))
    val x3412 = x3411 // FixConvert(x3411,TRUE,_64,_0)
    val x3413 = top.argFringe.dramAddress(x3324).name("x3413").ctrl(x3419) // GetDRAMAddress(x3324)
    val x3414 = OpDef(op=FixAdd, inputs=List(x3412, x3413)).name("x3414").ctrl(x3419) // FixAdd(x3412,x3413)
    val x3416_x3415 = x3414 // FixConvert(x3414,TRUE,_64,_0)
    // x3416 = SimpleStruct(ArrayBuffer((offset,x3415), (size,Const(384)), (isLoad,Const(true))))
    val x3417 = OpDef(op=BitAnd, inputs=List(b2089, b2059)).name("x3417").ctrl(x3419) // And(b2089,b2059)
    val b3588_b3586 = WriteMem(b3586, x3416_x3415).name("b3588_b3586").ctrl(x3419) // StreamWrite(x3404,x3416,x3417)
    val b3589_b3587 = WriteMem(b3587, Const(384)).name("b3589_b3587").ctrl(x3419) // StreamWrite(x3404,x3416,x3417)
    val x3420 = FringeContainer(x3324,b3586,b3587,x3405).name("x3420").ctrl(x3429) // FringeDenseLoad(x3324,x3404,x3405)
    val x3421 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3421").ctrl(x3429) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3422 = CounterChain(List(x3421)).name("x3422").ctrl(x3429) // CounterChainNew(List(x3421))
    val x3428 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3422).name("x3428").ctrl(x3429) // UnrolledForeach(List(b2089, b2059),x3422,Block(Const(())),List(List(b2109)),List(List(b2110)))
    val b2109 = CounterIter(x3421, None).ctrl(x3428).name("b2109")
    val b2110 = DummyOp().ctrl(x3428).name("b2110")
    val x3423 = OpDef(op=BitAnd, inputs=List(b2110, b2089)).name("x3423").ctrl(x3428) // And(b2110,b2089)
    val x3424 = OpDef(op=BitAnd, inputs=List(x3423, b2059)).name("x3424").ctrl(x3428) // And(x3423,b2059)
    val x3425_x3425 = ReadMem(x3405).name("x3425").ctrl(x3428) // ParStreamRead(x3405,List(x3424))
    val x3426_x3426 = x3425_x3425 // x3426 = VectorApply(x3425,0)
    val x3427 = StoreBanks(List(x3379_d0_b0), List(b2088, b2109), x3426_x3426).name("x3427").ctrl(x3428) // ParSRAMStore(x3379,List(List(b2088, b2109)),List(x3426),List(x3424))
    val x3430_d0_b0 = SRAM(size=9216, banking=NoBanking()).name("x3430_d0_b0").ctrl(x3492) // x3430 = SRAMNew(ArrayBuffer(Const(96), Const(96)))
    isAccum(x3430_d0_b0) = false
    bufferDepthOf(x3430_d0_b0) = 2
    val x3430_d1_b0 = SRAM(size=9216, banking=NoBanking()).name("x3430_d1_b0").ctrl(x3492) // x3430 = SRAMNew(ArrayBuffer(Const(96), Const(96)))
    isAccum(x3430_d1_b0) = true
    bufferDepthOf(x3430_d1_b0) = 1
    val x3431 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3431").ctrl(x3492) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3432 = CounterChain(List(x3431)).name("x3432").ctrl(x3492) // CounterChainNew(List(x3431))
    val x3478 = LoopController(style=MetaPipe, level=OuterControl, cchain=x3432).name("x3478").ctrl(x3492) // UnrolledReduce(List(b2059),x3432,x3430,Block((x3430) => Const(())),List(List(b2125)),List(List(b2126)))
    val b2125 = CounterIter(x3431, Some(0)).ctrl(x3478).name("b2125")
    val b2126 = DummyOp().ctrl(x3478).name("b2126")
    val x3433_d0_b0 = SRAM(size=6, banking=Strided(banks=16, stride=1)).name("x3433_d0_b0").ctrl(x3478) // x3433 = SRAMNew(ArrayBuffer(Const(96)))
    isAccum(x3433_d0_b0) = false
    bufferDepthOf(x3433_d0_b0) = 2
    val x3433_d1_b0 = SRAM(size=6, banking=Strided(banks=16, stride=1)).name("x3433_d1_b0").ctrl(x3478) // x3433 = SRAMNew(ArrayBuffer(Const(96)))
    isAccum(x3433_d1_b0) = false
    bufferDepthOf(x3433_d1_b0) = 2
    val x3434_d0_b0 = SRAM(size=576, banking=Strided(banks=16, stride=1)).name("x3434_d0_b0").ctrl(x3478) // x3434 = SRAMNew(ArrayBuffer(Const(96), Const(96)))
    isAccum(x3434_d0_b0) = false
    bufferDepthOf(x3434_d0_b0) = 2
    val x3435 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3435").ctrl(x3478) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3436 = CounterChain(List(x3435)).name("x3436").ctrl(x3478) // CounterChainNew(List(x3435))
    val x3450 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3436).name("x3450").ctrl(x3478) // UnrolledForeach(List(b2126, b2059),x3436,Block(Const(())),List(List(b2131)),List(List(b2132)))
    val b2131 = CounterIter(x3435, None).ctrl(x3450).name("b2131")
    val b2132 = DummyOp().ctrl(x3450).name("b2132")
    val x3437 = OpDef(op=BitAnd, inputs=List(b2132, b2126)).name("x3437").ctrl(x3450) // And(b2132,b2126)
    val x3438 = OpDef(op=BitAnd, inputs=List(x3437, b2059)).name("x3438").ctrl(x3450) // And(x3437,b2059)
    val x3439 = LoadBanks(List(x3379_d0_b0), List(b2125, b2131)).name("x3439").ctrl(x3450) // ParSRAMLoad(x3379,List(List(b2125, b2131)),List(x3438))
    val x3440 = x3439 // x3440 = VectorApply(x3439,0)
    val x3441 = LoadBanks(List(x3378_d0_b0), List(b2125)).name("x3441").ctrl(x3450) // SRAMLoad(x3378,ArrayBuffer(Const(32)),List(b2125),Const(0),x3438)
    val x3442 = OpDef(op=FixEql, inputs=List(x3441, Const(1))).name("x3442").ctrl(x3450) // FixEql(x3441,Const(1))
    val x3443 = LoadBanks(List(x3335_d0_b0), List(b2131)).name("x3443").ctrl(x3450) // ParSRAMLoad(x3335,List(List(b2131)),List(x3438))
    val x3444 = x3443 // x3444 = VectorApply(x3443,0)
    val x3445 = LoadBanks(List(x3334_d0_b0), List(b2131)).name("x3445").ctrl(x3450) // ParSRAMLoad(x3334,List(List(b2131)),List(x3438))
    val x3446 = x3445 // x3446 = VectorApply(x3445,0)
    val x3447 = OpDef(op=MuxOp, inputs=List(x3442, x3444, x3446)).name("x3447").ctrl(x3450) // Mux(x3442,x3444,x3446)
    val x3448 = OpDef(op=FixSub, inputs=List(x3440, x3447)).name("x3448").ctrl(x3450) // FixSub(x3440,x3447)
    val x3449 = StoreBanks(List(x3433_d0_b0, x3433_d1_b0), List(b2131), x3448).name("x3449").ctrl(x3450) // ParSRAMStore(x3433,List(List(b2131)),List(x3448),List(x3438))
    val x3451 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3451").ctrl(x3478) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3452 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3452").ctrl(x3478) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3453 = CounterChain(List(x3452,x3451)).name("x3453").ctrl(x3478) // CounterChainNew(List(x3452, x3451))
    val x3462 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3453).name("x3462").ctrl(x3478) // UnrolledForeach(List(b2126, b2059),x3453,Block(Const(())),List(List(b2150), List(b2151)),List(List(b2152), List(b2153)))
    val b2150 = CounterIter(x3452, Some(0)).ctrl(x3462).name("b2150")
    val b2152 = DummyOp().ctrl(x3462).name("b2152")
    val b2151 = CounterIter(x3451, None).ctrl(x3462).name("b2151")
    val b2153 = DummyOp().ctrl(x3462).name("b2153")
    val x3454 = OpDef(op=BitAnd, inputs=List(b2152, b2153)).name("x3454").ctrl(x3462) // And(b2152,b2153)
    val x3455 = OpDef(op=BitAnd, inputs=List(b2126, b2059)).name("x3455").ctrl(x3462) // And(b2126,b2059)
    val x3456 = OpDef(op=BitAnd, inputs=List(x3454, x3455)).name("x3456").ctrl(x3462) // And(x3454,x3455)
    val x3457 = LoadBanks(List(x3433_d1_b0), List(b2150)).name("x3457").ctrl(x3462) // SRAMLoad(x3433,ArrayBuffer(Const(96)),List(b2150),Const(0),x3456)
    val x3458 = LoadBanks(List(x3433_d0_b0), List(b2151)).name("x3458").ctrl(x3462) // ParSRAMLoad(x3433,List(List(b2151)),List(x3456))
    val x3459 = x3458 // x3459 = VectorApply(x3458,0)
    val x3460 = OpDef(op=FixMul, inputs=List(x3457, x3459)).name("x3460").ctrl(x3462) // FixMul(x3457,x3459)
    val x3461 = StoreBanks(List(x3434_d0_b0), List(b2150, b2151), x3460).name("x3461").ctrl(x3462) // ParSRAMStore(x3434,List(List(b2150, b2151)),List(x3460),List(x3456))
    val x3463 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3463").ctrl(x3478) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3464 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3464").ctrl(x3478) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3465 = CounterChain(List(x3464,x3463)).name("x3465").ctrl(x3478) // CounterChainNew(ArrayBuffer(x3464, x3463))
    val x3477 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3465).name("x3477").ctrl(x3478) // UnrolledForeach(List(),x3465,Block(Const(())),ArrayBuffer(List(b2163), List(b2164)),ArrayBuffer(List(b2165), List(b2166)))
    val b2163 = CounterIter(x3464, Some(0)).ctrl(x3477).name("b2163")
    val b2165 = DummyOp().ctrl(x3477).name("b2165")
    val b2164 = CounterIter(x3463, None).ctrl(x3477).name("b2164")
    val b2166 = DummyOp().ctrl(x3477).name("b2166")
    val x3466 = OpDef(op=BitAnd, inputs=List(b2165, b2166)).name("x3466").ctrl(x3477) // And(b2165,b2166)
    val x3467 = OpDef(op=BitAnd, inputs=List(x3466, b2059)).name("x3467").ctrl(x3477) // And(x3466,b2059)
    val x3468 = LoadBanks(List(x3434_d0_b0), List(b2163, b2164)).name("x3468").ctrl(x3477) // ParSRAMLoad(x3434,List(ArrayBuffer(b2163, b2164)),List(x3467))
    val x3469 = x3468 // x3469 = VectorApply(x3468,0)
    val x3470 = LoadBanks(List(x3430_d1_b0), List(b2163, b2164)).name("x3470").ctrl(x3477) // ParSRAMLoad(x3430,List(ArrayBuffer(b2163, b2164)),List(x3467))
    val x3471 = x3470 // x3471 = VectorApply(x3470,0)
    val x3472 = OpDef(op=BitAnd, inputs=List(b2126, b2059)).name("x3472").ctrl(x3477) // And(b2126,b2059)
    val x3473 = OpDef(op=BitAnd, inputs=List(x3472, x3467)).name("x3473").ctrl(x3477) // And(x3472,x3467)
    val x3474 = OpDef(op=FixEql, inputs=List(b2125, Const(0))).name("x3474").ctrl(x3477) // FixEql(b2125,Const(0))
    val x3475 = ReduceAccumOp(op=FixAdd, input=x3469, accum=x3471).name("x3475").ctrl(x3477) // FixAdd(x3469,x3471)
    val x3476 = StoreBanks(List(x3430_d0_b0, x3430_d1_b0), List(b2163, b2164), x3475).name("x3476").ctrl(x3477) // ParSRAMStore(x3430,List(ArrayBuffer(b2163, b2164)),List(x3475),List(x3467))
    val x3479 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3479").ctrl(x3492) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3480 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3480").ctrl(x3492) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3481 = CounterChain(List(x3480,x3479)).name("x3481").ctrl(x3492) // CounterChainNew(ArrayBuffer(x3480, x3479))
    val x3491 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3481).name("x3491").ctrl(x3492) // UnrolledForeach(List(),x3481,Block(Const(())),ArrayBuffer(List(b2180), List(b2181)),ArrayBuffer(List(b2182), List(b2183)))
    val b2180 = CounterIter(x3480, Some(0)).ctrl(x3491).name("b2180")
    val b2182 = DummyOp().ctrl(x3491).name("b2182")
    val b2181 = CounterIter(x3479, None).ctrl(x3491).name("b2181")
    val b2183 = DummyOp().ctrl(x3491).name("b2183")
    val x3482 = OpDef(op=BitAnd, inputs=List(b2182, b2183)).name("x3482").ctrl(x3491) // And(b2182,b2183)
    val x3483 = LoadBanks(List(x3430_d0_b0), List(b2180, b2181)).name("x3483").ctrl(x3491) // ParSRAMLoad(x3430,List(ArrayBuffer(b2180, b2181)),List(x3482))
    val x3484 = x3483 // x3484 = VectorApply(x3483,0)
    val x3485 = LoadBanks(List(x3374_d1_b0), List(b2180, b2181)).name("x3485").ctrl(x3491) // ParSRAMLoad(x3374,List(ArrayBuffer(b2180, b2181)),List(x3482))
    val x3486 = x3485 // x3486 = VectorApply(x3485,0)
    val x3487 = OpDef(op=BitAnd, inputs=List(b2059, x3482)).name("x3487").ctrl(x3491) // And(b2059,x3482)
    val x3488 = OpDef(op=FixEql, inputs=List(b2058, Const(0))).name("x3488").ctrl(x3491) // FixEql(b2058,Const(0))
    val x3489 = ReduceAccumOp(op=FixAdd, input=x3484, accum=x3486).name("x3489").ctrl(x3491) // FixAdd(x3484,x3486)
    val x3490 = StoreBanks(List(x3374_d0_b0, x3374_d1_b0), List(b2180, b2181), x3489).name("x3490").ctrl(x3491) // ParSRAMStore(x3374,List(ArrayBuffer(b2180, b2181)),List(x3489),List(x3482))
    val x3493 = Counter(min=Const(0), max=Const(96), step=Const(1), par=1).name("x3493").ctrl(x3522) // CounterNew(Const(0),Const(96),Const(1),Const(1))
    val x3494 = CounterChain(List(x3493)).name("x3494").ctrl(x3522) // CounterChainNew(List(x3493))
    val x3521 = LoopController(style=StreamPipe, level=OuterControl, cchain=x3494).name("x3521").ctrl(x3522) // UnrolledForeach(List(Const(true)),x3494,Block(Const(())),List(List(b2197)),List(List(b2198)))
    val b2197 = CounterIter(x3493, Some(0)).ctrl(x3521).name("b2197")
    val b2198 = DummyOp().ctrl(x3521).name("b2198")
    val b3590 = StreamOut(field="offset").name("b3590").ctrl(x3521) // x3495 = StreamOutNew(BurstCmdBus)
    val b3591 = StreamOut(field="size").name("b3591").ctrl(x3521) // x3495 = StreamOutNew(BurstCmdBus)
    val x3496 = StreamOut(field="data").name("x3496").ctrl(x3521) // x3496 = StreamOutNew(BurstFullDataBus())
    val x3497 = StreamIn(field="ack").name("x3497").ctrl(x3521) // x3497 = StreamInNew(BurstAckBus)
    val x3509 = UnitController(style=SeqPipe, level=InnerControl).name("x3509").ctrl(x3521) // UnitPipe(List(b2198),Block(x3508))
    val x3498 = b2197 // FixConvert(b2197,TRUE,_32,_0)
    val x3499 = OpDef(op=FixMul, inputs=List(x3498, Const(96))).name("x3499").ctrl(x3509) // FixMul(x3498,Const(96))
    val x3500 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x3501 = OpDef(op=FixAdd, inputs=List(x3499, x3500)).name("x3501").ctrl(x3509) // FixAdd(x3499,x3500)
    val x3502 = OpDef(op=FixSla, inputs=List(x3501, Const(2))).name("x3502").ctrl(x3509) // FixLsh(x3501,Const(2))
    val x3503 = x3502 // FixConvert(x3502,TRUE,_64,_0)
    val x3504 = top.argFringe.dramAddress(x3329).name("x3504").ctrl(x3509) // GetDRAMAddress(x3329)
    val x3505 = OpDef(op=FixAdd, inputs=List(x3503, x3504)).name("x3505").ctrl(x3509) // FixAdd(x3503,x3504)
    val x3507_x3506 = x3505 // FixConvert(x3505,TRUE,_64,_0)
    // x3507 = SimpleStruct(ArrayBuffer((offset,x3506), (size,Const(384)), (isLoad,Const(false))))
    val b3592_b3590 = WriteMem(b3590, x3507_x3506).name("b3592_b3590").ctrl(x3509) // StreamWrite(x3495,x3507,b2198)
    val b3593_b3591 = WriteMem(b3591, Const(384)).name("b3593_b3591").ctrl(x3509) // StreamWrite(x3495,x3507,b2198)
    val x3510 = Counter(min=Const(0), max=Const(96), step=Const(1), par=16).name("x3510").ctrl(x3521) // CounterNew(Const(0),Const(96),Const(1),Const(16))
    val x3511 = CounterChain(List(x3510)).name("x3511").ctrl(x3521) // CounterChainNew(List(x3510))
    val x3517 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3511).name("x3517").ctrl(x3521) // UnrolledForeach(List(b2198),x3511,Block(Const(())),List(List(b2216)),List(List(b2217)))
    val b2216 = CounterIter(x3510, None).ctrl(x3517).name("b2216")
    val b2217 = DummyOp().ctrl(x3517).name("b2217")
    val x3512 = OpDef(op=BitAnd, inputs=List(b2217, b2198)).name("x3512").ctrl(x3517) // And(b2217,b2198)
    val x3513 = LoadBanks(List(x3374_d0_b0), List(b2197, b2216)).name("x3513").ctrl(x3517) // ParSRAMLoad(x3374,List(List(b2197, b2216)),List(x3512))
    val x3515_x3514 = x3513 // x3514 = VectorApply(x3513,0)
    // x3515 = SimpleStruct(ArrayBuffer((_1,x3514), (_2,Const(true))))
    val x3516_x3496 = WriteMem(x3496, x3515_x3514).name("x3516_x3496").ctrl(x3517) // ParStreamWrite(x3496,List(x3515),List(x3512))
    val x3518 = FringeContainer(x3329,b3590,b3591,x3496,x3497).name("x3518").ctrl(x3521) // FringeDenseStore(x3329,x3495,x3496,x3497)
    val x3520 = UnitController(style=SeqPipe, level=InnerControl).name("x3520").ctrl(x3521) // UnitPipe(List(b2198),Block(Const(())))
    val x3519_x3519 = ReadMem(x3497).name("x3519").ctrl(x3520) // StreamRead(x3497,b2198)
    
  }
}
