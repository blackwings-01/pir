import pir.node._
import prism.enums._

object SYRK_col extends PIRApp {
  def main(implicit design:PIRDesign) = {
    import design.pirmeta._
    val x3897_d0 = top.argFringe.argIn(init=0).name("x3897_d0").ctrl(top) // ArgInNew(Const(0))
    val x3899 = ReadMem(x3897_d0).name("x3899").ctrl(top) // RegRead(x3897)
    val x3900 = ReadMem(x3897_d0).name("x3900").ctrl(top) // RegRead(x3897)
    val x3901 = DRAM().name("x3901").ctrl(top) // x3901 = DRAMNew(ArrayBuffer(x3900, x3899),Const(0))
    val x3902 = ReadMem(x3897_d0).name("x3902").ctrl(top) // RegRead(x3897)
    val x3903 = DRAM().name("x3903").ctrl(top) // x3903 = DRAMNew(ArrayBuffer(x3902, Const(64)),Const(0))
    val x4193 = UnitController(style=SeqPipe, level=OuterControl).name("x4193").ctrl(top) // Hwblock(Block(Const(())),false)
    val x3906_d0_b0 = SRAM(size=2048, banking=NoBanking()).name("x3906_d0_b0").ctrl(x4193) // x3906 = SRAMNew(ArrayBuffer(Const(32), Const(64)))
    isAccum(x3906_d0_b0) = false
    val x3906_d1_b0 = SRAM(size=2048, banking=NoBanking()).name("x3906_d1_b0").ctrl(x4193) // x3906 = SRAMNew(ArrayBuffer(Const(32), Const(64)))
    isAccum(x3906_d1_b0) = false
    val x3906_d2_b0 = SRAM(size=2048, banking=NoBanking()).name("x3906_d2_b0").ctrl(x4193) // x3906 = SRAMNew(ArrayBuffer(Const(32), Const(64)))
    isAccum(x3906_d2_b0) = false
    val x3907_d0_b0 = SRAM(size=2048, banking=NoBanking()).name("x3907_d0_b0").ctrl(x4193) // x3907 = SRAMNew(ArrayBuffer(Const(32), Const(64)))
    isAccum(x3907_d0_b0) = false
    val x3908_d0_b0 = SRAM(size=1024, banking=NoBanking()).name("x3908_d0_b0").ctrl(x4193) // x3908 = SRAMNew(ArrayBuffer(Const(32), Const(32)))
    isAccum(x3908_d0_b0) = false
    val x3908_d1_b0 = SRAM(size=1024, banking=NoBanking()).name("x3908_d1_b0").ctrl(x4193) // x3908 = SRAMNew(ArrayBuffer(Const(32), Const(32)))
    isAccum(x3908_d1_b0) = true
    val x3908_d2_b0 = SRAM(size=1024, banking=NoBanking()).name("x3908_d2_b0").ctrl(x4193) // x3908 = SRAMNew(ArrayBuffer(Const(32), Const(32)))
    isAccum(x3908_d2_b0) = false
    val x3908_d3_b0 = SRAM(size=1024, banking=NoBanking()).name("x3908_d3_b0").ctrl(x4193) // x3908 = SRAMNew(ArrayBuffer(Const(32), Const(32)))
    isAccum(x3908_d3_b0) = true
    val x3909 = ReadMem(x3897_d0).name("x3909").ctrl(x4193) // RegRead(x3897)
    val x3910 = Counter(min=Const(0), max=x3909, step=Const(32), par=1).name("x3910").ctrl(x4193) // CounterNew(Const(0),x3909,Const(32),Const(1))
    val x3911 = CounterChain(List(x3910)).name("x3911").ctrl(x4193) // CounterChainNew(List(x3910))
    val x4192 = LoopController(style=SeqPipe, level=OuterControl, cchain=x3911).name("x4192").ctrl(x4193) // UnrolledForeach(List(Const(true)),x3911,Block(Const(())),List(List(b2212)),List(List(b2213)))
    val b2212 = CounterIter(x3910, Some(0)).ctrl(x4192).name("b2212")
    val b2213 = DummyOp().ctrl(x4192).name("b2213")
    val x3913 = UnitController(style=SeqPipe, level=InnerControl).name("x3913").ctrl(x4192) // UnitPipe(List(b2213),Block(Const(())))
    val x3912 = OpDef(op=FixAdd, inputs=List(b2212, Const(32))).name("x3912").ctrl(x3913) // FixAdd(b2212,Const(32))
    val x3914 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3914").ctrl(x4192) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3915 = CounterChain(List(x3914)).name("x3915").ctrl(x4192) // CounterChainNew(List(x3914))
    val x3941 = LoopController(style=StreamPipe, level=OuterControl, cchain=x3915).name("x3941").ctrl(x4192) // UnrolledForeach(List(b2213),x3915,Block(Const(())),List(List(b2218)),List(List(b2219)))
    val b2218 = CounterIter(x3914, Some(0)).ctrl(x3941).name("b2218")
    val b2219 = DummyOp().ctrl(x3941).name("b2219")
    val b4229 = StreamOut(field="offset").name("b4229").ctrl(x3941) // x3916 = StreamOutNew(BurstCmdBus)
    val b4230 = StreamOut(field="size").name("b4230").ctrl(x3941) // x3916 = StreamOutNew(BurstCmdBus)
    val x3917 = StreamIn(field="data").name("x3917").ctrl(x3941) // x3917 = StreamInNew(BurstDataBus())
    val x3931 = UnitController(style=SeqPipe, level=InnerControl).name("x3931").ctrl(x3941) // UnitPipe(List(b2219, b2213),Block(x3930))
    val x3918 = OpDef(op=FixAdd, inputs=List(b2212, b2218)).name("x3918").ctrl(x3931) // FixAdd(b2212,b2218)
    val x3919 = x3918 // FixConvert(x3918,TRUE,_32,_0)
    val x3920 = OpDef(op=FixSla, inputs=List(x3919, Const(6))).name("x3920").ctrl(x3931) // FixLsh(x3919,Const(6))
    val x3921 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x3922 = OpDef(op=FixAdd, inputs=List(x3920, x3921)).name("x3922").ctrl(x3931) // FixAdd(x3920,x3921)
    val x3923 = OpDef(op=FixSla, inputs=List(x3922, Const(2))).name("x3923").ctrl(x3931) // FixLsh(x3922,Const(2))
    val x3924 = x3923 // FixConvert(x3923,TRUE,_64,_0)
    val x3925 = top.argFringe.dramAddress(x3903).name("x3925").ctrl(x3931) // GetDRAMAddress(x3903)
    val x3926 = OpDef(op=FixAdd, inputs=List(x3924, x3925)).name("x3926").ctrl(x3931) // FixAdd(x3924,x3925)
    val x3928_x3927 = x3926 // FixConvert(x3926,TRUE,_64,_0)
    // x3928 = SimpleStruct(ArrayBuffer((offset,x3927), (size,Const(256)), (isLoad,Const(true))))
    val x3929 = OpDef(op=BitAnd, inputs=List(b2219, b2213)).name("x3929").ctrl(x3931) // And(b2219,b2213)
    val b4231_b4229 = WriteMem(b4229, x3928_x3927).name("b4231_b4229").ctrl(x3931) // StreamWrite(x3916,x3928,x3929)
    val b4232_b4230 = WriteMem(b4230, Const(256)).name("b4232_b4230").ctrl(x3931) // StreamWrite(x3916,x3928,x3929)
    val x3932 = FringeContainer(x3903,b4229,b4230,x3917).name("x3932").ctrl(x3941) // FringeDenseLoad(x3903,x3916,x3917)
    val x3933 = Counter(min=Const(0), max=Const(64), step=Const(1), par=1).name("x3933").ctrl(x3941) // CounterNew(Const(0),Const(64),Const(1),Const(1))
    val x3934 = CounterChain(List(x3933)).name("x3934").ctrl(x3941) // CounterChainNew(List(x3933))
    val x3940 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3934).name("x3940").ctrl(x3941) // UnrolledForeach(List(b2219, b2213),x3934,Block(Const(())),List(List(b2239)),List(List(b2240)))
    val b2239 = CounterIter(x3933, None).ctrl(x3940).name("b2239")
    val b2240 = DummyOp().ctrl(x3940).name("b2240")
    val x3935 = OpDef(op=BitAnd, inputs=List(b2240, b2219)).name("x3935").ctrl(x3940) // And(b2240,b2219)
    val x3936 = OpDef(op=BitAnd, inputs=List(x3935, b2213)).name("x3936").ctrl(x3940) // And(x3935,b2213)
    val x3937_x3937 = ReadMem(x3917).name("x3937").ctrl(x3940) // ParStreamRead(x3917,List(x3936))
    val x3938_x3938 = x3937_x3937 // x3938 = VectorApply(x3937,0)
    val x3939 = StoreBanks(List(x3906_d0_b0, x3906_d1_b0, x3906_d2_b0), List(b2218, b2239), x3938_x3938).name("x3939").ctrl(x3940) // ParSRAMStore(x3906,List(List(b2218, b2239)),List(x3938),List(x3936))
    val x3942 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3942").ctrl(x4192) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3943 = CounterChain(List(x3942)).name("x3943").ctrl(x4192) // CounterChainNew(List(x3942))
    val x3970 = LoopController(style=StreamPipe, level=OuterControl, cchain=x3943).name("x3970").ctrl(x4192) // UnrolledForeach(List(b2213),x3943,Block(Const(())),List(List(b2250)),List(List(b2251)))
    val b2250 = CounterIter(x3942, Some(0)).ctrl(x3970).name("b2250")
    val b2251 = DummyOp().ctrl(x3970).name("b2251")
    val b4233 = StreamOut(field="offset").name("b4233").ctrl(x3970) // x3944 = StreamOutNew(BurstCmdBus)
    val b4234 = StreamOut(field="size").name("b4234").ctrl(x3970) // x3944 = StreamOutNew(BurstCmdBus)
    val x3945 = StreamIn(field="data").name("x3945").ctrl(x3970) // x3945 = StreamInNew(BurstDataBus())
    val x3960 = UnitController(style=SeqPipe, level=InnerControl).name("x3960").ctrl(x3970) // UnitPipe(List(b2251, b2213),Block(x3959))
    val x3946 = OpDef(op=FixAdd, inputs=List(b2212, b2250)).name("x3946").ctrl(x3960) // FixAdd(b2212,b2250)
    val x3947 = x3946 // FixConvert(x3946,TRUE,_32,_0)
    val x3948 = ReadMem(x3897_d0).name("x3948").ctrl(x3960) // RegRead(x3897)
    val x3949 = OpDef(op=FixMul, inputs=List(x3947, x3948)).name("x3949").ctrl(x3960) // FixMul(x3947,x3948)
    val x3950 = b2212 // FixConvert(b2212,TRUE,_32,_0)
    val x3951 = OpDef(op=FixAdd, inputs=List(x3949, x3950)).name("x3951").ctrl(x3960) // FixAdd(x3949,x3950)
    val x3952 = OpDef(op=FixSla, inputs=List(x3951, Const(2))).name("x3952").ctrl(x3960) // FixLsh(x3951,Const(2))
    val x3953 = x3952 // FixConvert(x3952,TRUE,_64,_0)
    val x3954 = top.argFringe.dramAddress(x3901).name("x3954").ctrl(x3960) // GetDRAMAddress(x3901)
    val x3955 = OpDef(op=FixAdd, inputs=List(x3953, x3954)).name("x3955").ctrl(x3960) // FixAdd(x3953,x3954)
    val x3957_x3956 = x3955 // FixConvert(x3955,TRUE,_64,_0)
    // x3957 = SimpleStruct(ArrayBuffer((offset,x3956), (size,Const(128)), (isLoad,Const(true))))
    val x3958 = OpDef(op=BitAnd, inputs=List(b2251, b2213)).name("x3958").ctrl(x3960) // And(b2251,b2213)
    val b4235_b4233 = WriteMem(b4233, x3957_x3956).name("b4235_b4233").ctrl(x3960) // StreamWrite(x3944,x3957,x3958)
    val b4236_b4234 = WriteMem(b4234, Const(128)).name("b4236_b4234").ctrl(x3960) // StreamWrite(x3944,x3957,x3958)
    val x3961 = FringeContainer(x3901,b4233,b4234,x3945).name("x3961").ctrl(x3970) // FringeDenseLoad(x3901,x3944,x3945)
    val x3962 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3962").ctrl(x3970) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3963 = CounterChain(List(x3962)).name("x3963").ctrl(x3970) // CounterChainNew(List(x3962))
    val x3969 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3963).name("x3969").ctrl(x3970) // UnrolledForeach(List(b2251, b2213),x3963,Block(Const(())),List(List(b2272)),List(List(b2273)))
    val b2272 = CounterIter(x3962, None).ctrl(x3969).name("b2272")
    val b2273 = DummyOp().ctrl(x3969).name("b2273")
    val x3964 = OpDef(op=BitAnd, inputs=List(b2273, b2251)).name("x3964").ctrl(x3969) // And(b2273,b2251)
    val x3965 = OpDef(op=BitAnd, inputs=List(x3964, b2213)).name("x3965").ctrl(x3969) // And(x3964,b2213)
    val x3966_x3966 = ReadMem(x3945).name("x3966").ctrl(x3969) // ParStreamRead(x3945,List(x3965))
    val x3967_x3967 = x3966_x3966 // x3967 = VectorApply(x3966,0)
    val x3968 = StoreBanks(List(x3908_d0_b0, x3908_d1_b0, x3908_d2_b0, x3908_d3_b0), List(b2250, b2272), x3967_x3967).name("x3968").ctrl(x3969) // ParSRAMStore(x3908,List(List(b2250, b2272)),List(x3967),List(x3965))
    val x3971 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x3971").ctrl(x4192) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x3972 = CounterChain(List(x3971)).name("x3972").ctrl(x4192) // CounterChainNew(List(x3971))
    val x4007 = LoopController(style=SeqPipe, level=OuterControl, cchain=x3972).name("x4007").ctrl(x4192) // UnrolledForeach(List(b2213),x3972,Block(Const(())),List(List(b2283)),List(List(b2284)))
    val b2283 = CounterIter(x3971, Some(0)).ctrl(x4007).name("b2283")
    val b2284 = DummyOp().ctrl(x4007).name("b2284")
    val x3973 = Reg(init=0).name("x3973").ctrl(x4007) // x3973 = RegNew(Const(0))
    isAccum(x3973) = false
    val x3977 = UnitController(style=SeqPipe, level=InnerControl).name("x3977").ctrl(x4007) // UnitPipe(List(b2284, b2213),Block(Const(())))
    val x3974 = OpDef(op=FixAdd, inputs=List(b2283, Const(1))).name("x3974").ctrl(x3977) // FixAdd(b2283,Const(1))
    val x3975 = OpDef(op=BitAnd, inputs=List(b2284, b2213)).name("x3975").ctrl(x3977) // And(b2284,b2213)
    val x3976_x3973 = WriteMem(x3973, x3974).name("x3976_x3973").ctrl(x3977) // RegWrite(x3973,x3974,x3975)
    val x3978 = ReadMem(x3973).name("x3978").ctrl(x4007) // RegRead(x3973)
    val x3979 = Counter(min=Const(0), max=x3978, step=Const(1), par=1).name("x3979").ctrl(x4007) // CounterNew(Const(0),x3978,Const(1),Const(1))
    val x3980 = CounterChain(List(x3979)).name("x3980").ctrl(x4007) // CounterChainNew(List(x3979))
    val x4006 = LoopController(style=SeqPipe, level=OuterControl, cchain=x3980).name("x4006").ctrl(x4007) // UnrolledForeach(List(b2284, b2213),x3980,Block(Const(())),List(List(b2293)),List(List(b2294)))
    val b2293 = CounterIter(x3979, Some(0)).ctrl(x4006).name("b2293")
    val b2294 = DummyOp().ctrl(x4006).name("b2294")
    val x3981_d0 = Reg(init=0).name("x3981_d0").ctrl(x4006) // x3981 = RegNew(Const(0))
    isAccum(x3981_d0) = false
    val x3981_d1 = Reg(init=0).name("x3981_d1").ctrl(x4006) // x3981 = RegNew(Const(0))
    isAccum(x3981_d1) = true
    val x3982 = Counter(min=Const(0), max=Const(64), step=Const(1), par=1).name("x3982").ctrl(x4006) // CounterNew(Const(0),Const(64),Const(1),Const(1))
    val x3983 = CounterChain(List(x3982)).name("x3983").ctrl(x4006) // CounterChainNew(List(x3982))
    val x3998 = LoopController(style=InnerPipe, level=InnerControl, cchain=x3983).name("x3998").ctrl(x4006) // UnrolledReduce(List(b2294, b2284, b2213),x3983,x3981,Block((x3981) => Const(())),List(List(b2298)),List(List(b2299)))
    val b2298 = CounterIter(x3982, None).ctrl(x3998).name("b2298")
    val b2299 = DummyOp().ctrl(x3998).name("b2299")
    val x3984 = OpDef(op=BitAnd, inputs=List(b2299, b2294)).name("x3984").ctrl(x3998) // And(b2299,b2294)
    val x3985 = OpDef(op=BitAnd, inputs=List(b2284, b2213)).name("x3985").ctrl(x3998) // And(b2284,b2213)
    val x3986 = OpDef(op=BitAnd, inputs=List(x3984, x3985)).name("x3986").ctrl(x3998) // And(x3984,x3985)
    val x3987 = LoadBanks(List(x3906_d2_b0), List(b2283, b2298)).name("x3987").ctrl(x3998) // ParSRAMLoad(x3906,List(List(b2283, b2298)),List(x3986))
    val x3988 = x3987 // x3988 = VectorApply(x3987,0)
    val x3989 = LoadBanks(List(x3906_d1_b0), List(b2293, b2298)).name("x3989").ctrl(x3998) // ParSRAMLoad(x3906,List(List(b2293, b2298)),List(x3986))
    val x3990 = x3989 // x3990 = VectorApply(x3989,0)
    val x3991 = OpDef(op=FixMul, inputs=List(x3988, x3990)).name("x3991").ctrl(x3998) // FixMul(x3988,x3990)
    val x3992 = ReadMem(x3981_d1).name("x3992").ctrl(x3998) // RegRead(x3981)
    val x3993 = OpDef(op=FixEql, inputs=List(b2298, Const(0))).name("x3993").ctrl(x3998) // FixEql(b2298,Const(0))
    val x3994 = ReduceAccumOp(op=FixAdd, input=x3991, accum=x3992).name("x3994").ctrl(x3998) // FixAdd(x3991,x3992)
    val x3995 = OpDef(op=BitAnd, inputs=List(b2294, b2284)).name("x3995").ctrl(x3998) // And(b2294,b2284)
    val x3996 = OpDef(op=BitAnd, inputs=List(x3995, b2213)).name("x3996").ctrl(x3998) // And(x3995,b2213)
    val x3997_x3981_d0 = WriteMem(x3981_d0, x3994).name("x3997_x3981_d0").ctrl(x3998) // RegWrite(x3981,x3994,x3996)
    val x3997_x3981_d1 = WriteMem(x3981_d1, x3994).name("x3997_x3981_d1").ctrl(x3998) // RegWrite(x3981,x3994,x3996)
    val x4005 = UnitController(style=SeqPipe, level=InnerControl).name("x4005").ctrl(x4006) // UnitPipe(List(b2294, b2284, b2213),Block(Const(())))
    val x3999 = OpDef(op=BitAnd, inputs=List(b2294, b2284)).name("x3999").ctrl(x4005) // And(b2294,b2284)
    val x4000 = OpDef(op=BitAnd, inputs=List(x3999, b2213)).name("x4000").ctrl(x4005) // And(x3999,b2213)
    val x4001 = LoadBanks(List(x3908_d3_b0), List(b2283, b2293)).name("x4001").ctrl(x4005) // SRAMLoad(x3908,ArrayBuffer(Const(32), Const(32)),List(b2283, b2293),Const(0),x4000)
    val x4002 = ReadMem(x3981_d0).name("x4002").ctrl(x4005) // RegRead(x3981)
    val x4003 = OpDef(op=FixAdd, inputs=List(x4001, x4002)).name("x4003").ctrl(x4005) // FixAdd(x4001,x4002)
    val x4004 = StoreBanks(List(x3908_d0_b0, x3908_d1_b0, x3908_d2_b0, x3908_d3_b0), List(b2283, b2293), x4003).name("x4004").ctrl(x4005) // SRAMStore(x3908,ArrayBuffer(Const(32), Const(32)),List(b2283, b2293),Const(0),x4003,x4000)
    val x4008 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4008").ctrl(x4192) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4009 = CounterChain(List(x4008)).name("x4009").ctrl(x4192) // CounterChainNew(List(x4008))
    val x4041 = LoopController(style=StreamPipe, level=OuterControl, cchain=x4009).name("x4041").ctrl(x4192) // UnrolledForeach(List(b2213),x4009,Block(Const(())),List(List(b2326)),List(List(b2327)))
    val b2326 = CounterIter(x4008, Some(0)).ctrl(x4041).name("b2326")
    val b2327 = DummyOp().ctrl(x4041).name("b2327")
    val b4237 = StreamOut(field="offset").name("b4237").ctrl(x4041) // x4010 = StreamOutNew(BurstCmdBus)
    val b4238 = StreamOut(field="size").name("b4238").ctrl(x4041) // x4010 = StreamOutNew(BurstCmdBus)
    val x4011 = StreamOut(field="data").name("x4011").ctrl(x4041) // x4011 = StreamOutNew(BurstFullDataBus())
    val x4012 = StreamIn(field="ack").name("x4012").ctrl(x4041) // x4012 = StreamInNew(BurstAckBus)
    val x4027 = UnitController(style=SeqPipe, level=InnerControl).name("x4027").ctrl(x4041) // UnitPipe(List(b2327, b2213),Block(x4026))
    val x4013 = OpDef(op=FixAdd, inputs=List(b2212, b2326)).name("x4013").ctrl(x4027) // FixAdd(b2212,b2326)
    val x4014 = x4013 // FixConvert(x4013,TRUE,_32,_0)
    val x4015 = ReadMem(x3897_d0).name("x4015").ctrl(x4027) // RegRead(x3897)
    val x4016 = OpDef(op=FixMul, inputs=List(x4014, x4015)).name("x4016").ctrl(x4027) // FixMul(x4014,x4015)
    val x4017 = b2212 // FixConvert(b2212,TRUE,_32,_0)
    val x4018 = OpDef(op=FixAdd, inputs=List(x4016, x4017)).name("x4018").ctrl(x4027) // FixAdd(x4016,x4017)
    val x4019 = OpDef(op=FixSla, inputs=List(x4018, Const(2))).name("x4019").ctrl(x4027) // FixLsh(x4018,Const(2))
    val x4020 = x4019 // FixConvert(x4019,TRUE,_64,_0)
    val x4021 = top.argFringe.dramAddress(x3901).name("x4021").ctrl(x4027) // GetDRAMAddress(x3901)
    val x4022 = OpDef(op=FixAdd, inputs=List(x4020, x4021)).name("x4022").ctrl(x4027) // FixAdd(x4020,x4021)
    val x4024_x4023 = x4022 // FixConvert(x4022,TRUE,_64,_0)
    // x4024 = SimpleStruct(ArrayBuffer((offset,x4023), (size,Const(128)), (isLoad,Const(false))))
    val x4025 = OpDef(op=BitAnd, inputs=List(b2327, b2213)).name("x4025").ctrl(x4027) // And(b2327,b2213)
    val b4239_b4237 = WriteMem(b4237, x4024_x4023).name("b4239_b4237").ctrl(x4027) // StreamWrite(x4010,x4024,x4025)
    val b4240_b4238 = WriteMem(b4238, Const(128)).name("b4240_b4238").ctrl(x4027) // StreamWrite(x4010,x4024,x4025)
    val x4028 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4028").ctrl(x4041) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4029 = CounterChain(List(x4028)).name("x4029").ctrl(x4041) // CounterChainNew(List(x4028))
    val x4036 = LoopController(style=InnerPipe, level=InnerControl, cchain=x4029).name("x4036").ctrl(x4041) // UnrolledForeach(List(b2327, b2213),x4029,Block(Const(())),List(List(b2348)),List(List(b2349)))
    val b2348 = CounterIter(x4028, None).ctrl(x4036).name("b2348")
    val b2349 = DummyOp().ctrl(x4036).name("b2349")
    val x4030 = OpDef(op=BitAnd, inputs=List(b2349, b2327)).name("x4030").ctrl(x4036) // And(b2349,b2327)
    val x4031 = OpDef(op=BitAnd, inputs=List(x4030, b2213)).name("x4031").ctrl(x4036) // And(x4030,b2213)
    val x4032 = LoadBanks(List(x3908_d2_b0), List(b2326, b2348)).name("x4032").ctrl(x4036) // ParSRAMLoad(x3908,List(List(b2326, b2348)),List(x4031))
    val x4034_x4033 = x4032 // x4033 = VectorApply(x4032,0)
    // x4034 = SimpleStruct(ArrayBuffer((_1,x4033), (_2,Const(true))))
    val x4035_x4011 = WriteMem(x4011, x4034_x4033).name("x4035_x4011").ctrl(x4036) // ParStreamWrite(x4011,List(x4034),List(x4031))
    val x4037 = FringeContainer(x3901,b4237,b4238,x4011,x4012).name("x4037").ctrl(x4041) // FringeDenseStore(x3901,x4010,x4011,x4012)
    val x4040 = UnitController(style=SeqPipe, level=InnerControl).name("x4040").ctrl(x4041) // UnitPipe(List(b2327, b2213),Block(Const(())))
    val x4038 = OpDef(op=BitAnd, inputs=List(b2327, b2213)).name("x4038").ctrl(x4040) // And(b2327,b2213)
    val x4039_x4039 = ReadMem(x4012).name("x4039").ctrl(x4040) // StreamRead(x4012,x4038)
    val x4042 = Reg(init=0).name("x4042").ctrl(x4192) // x4042 = RegNew(Const(0))
    isAccum(x4042) = false
    val x4047 = UnitController(style=SeqPipe, level=InnerControl).name("x4047").ctrl(x4192) // UnitPipe(List(b2213),Block(Const(())))
    val x4043 = ReadMem(x3897_d0).name("x4043").ctrl(x4047) // RegRead(x3897)
    val x4044 = OpDef(op=FixSub, inputs=List(x4043, b2212)).name("x4044").ctrl(x4047) // FixSub(x4043,b2212)
    val x4045 = OpDef(op=FixSub, inputs=List(x4044, Const(32))).name("x4045").ctrl(x4047) // FixSub(x4044,Const(32))
    val x4046_x4042 = WriteMem(x4042, x4045).name("x4046_x4042").ctrl(x4047) // RegWrite(x4042,x4045,b2213)
    val x4048 = ReadMem(x4042).name("x4048").ctrl(x4192) // RegRead(x4042)
    val x4049 = Counter(min=Const(0), max=x4048, step=Const(32), par=1).name("x4049").ctrl(x4192) // CounterNew(Const(0),x4048,Const(32),Const(1))
    val x4050 = CounterChain(List(x4049)).name("x4050").ctrl(x4192) // CounterChainNew(List(x4049))
    val x4191 = LoopController(style=SeqPipe, level=OuterControl, cchain=x4050).name("x4191").ctrl(x4192) // UnrolledForeach(List(b2213),x4050,Block(Const(())),List(List(b2371)),List(List(b2372)))
    val b2371 = CounterIter(x4049, Some(0)).ctrl(x4191).name("b2371")
    val b2372 = DummyOp().ctrl(x4191).name("b2372")
    val x4051_d0 = Reg(init=0).name("x4051_d0").ctrl(x4191) // x4051 = RegNew(Const(0))
    isAccum(x4051_d0) = false
    val x4051_d1 = Reg(init=0).name("x4051_d1").ctrl(x4191) // x4051 = RegNew(Const(0))
    isAccum(x4051_d1) = false
    val x4051_d2 = Reg(init=0).name("x4051_d2").ctrl(x4191) // x4051 = RegNew(Const(0))
    isAccum(x4051_d2) = false
    val x4057 = UnitController(style=SeqPipe, level=InnerControl).name("x4057").ctrl(x4191) // UnitPipe(List(b2372, b2213),Block(Const(())))
    val x4052 = OpDef(op=FixAdd, inputs=List(b2212, Const(32))).name("x4052").ctrl(x4057) // FixAdd(b2212,Const(32))
    val x4053 = OpDef(op=FixAdd, inputs=List(x4052, b2371)).name("x4053").ctrl(x4057) // FixAdd(x4052,b2371)
    val x4054 = OpDef(op=FixAdd, inputs=List(x4053, Const(32))).name("x4054").ctrl(x4057) // FixAdd(x4053,Const(32))
    val x4055 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4055").ctrl(x4057) // And(b2372,b2213)
    val x4056_x4051_d0 = WriteMem(x4051_d0, x4053).name("x4056_x4051_d0").ctrl(x4057) // RegWrite(x4051,x4053,x4055)
    val x4056_x4051_d1 = WriteMem(x4051_d1, x4053).name("x4056_x4051_d1").ctrl(x4057) // RegWrite(x4051,x4053,x4055)
    val x4056_x4051_d2 = WriteMem(x4051_d2, x4053).name("x4056_x4051_d2").ctrl(x4057) // RegWrite(x4051,x4053,x4055)
    val x4058 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4058").ctrl(x4191) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4059 = CounterChain(List(x4058)).name("x4059").ctrl(x4191) // CounterChainNew(List(x4058))
    val x4089 = LoopController(style=StreamPipe, level=OuterControl, cchain=x4059).name("x4089").ctrl(x4191) // UnrolledForeach(List(b2372, b2213),x4059,Block(Const(())),List(List(b2382)),List(List(b2383)))
    val b2382 = CounterIter(x4058, Some(0)).ctrl(x4089).name("b2382")
    val b2383 = DummyOp().ctrl(x4089).name("b2383")
    val b4241 = StreamOut(field="offset").name("b4241").ctrl(x4089) // x4060 = StreamOutNew(BurstCmdBus)
    val b4242 = StreamOut(field="size").name("b4242").ctrl(x4089) // x4060 = StreamOutNew(BurstCmdBus)
    val x4061 = StreamIn(field="data").name("x4061").ctrl(x4089) // x4061 = StreamInNew(BurstDataBus())
    val x4078 = UnitController(style=SeqPipe, level=InnerControl).name("x4078").ctrl(x4089) // UnitPipe(List(b2383, b2372, b2213),Block(x4077))
    val x4062 = ReadMem(x4051_d2).name("x4062").ctrl(x4078) // RegRead(x4051)
    val x4063 = OpDef(op=FixAdd, inputs=List(x4062, b2382)).name("x4063").ctrl(x4078) // FixAdd(x4062,b2382)
    val x4064 = x4063 // FixConvert(x4063,TRUE,_32,_0)
    val x4065 = ReadMem(x3897_d0).name("x4065").ctrl(x4078) // RegRead(x3897)
    val x4066 = OpDef(op=FixMul, inputs=List(x4064, x4065)).name("x4066").ctrl(x4078) // FixMul(x4064,x4065)
    val x4067 = b2212 // FixConvert(b2212,TRUE,_32,_0)
    val x4068 = OpDef(op=FixAdd, inputs=List(x4066, x4067)).name("x4068").ctrl(x4078) // FixAdd(x4066,x4067)
    val x4069 = OpDef(op=FixSla, inputs=List(x4068, Const(2))).name("x4069").ctrl(x4078) // FixLsh(x4068,Const(2))
    val x4070 = x4069 // FixConvert(x4069,TRUE,_64,_0)
    val x4071 = top.argFringe.dramAddress(x3901).name("x4071").ctrl(x4078) // GetDRAMAddress(x3901)
    val x4072 = OpDef(op=FixAdd, inputs=List(x4070, x4071)).name("x4072").ctrl(x4078) // FixAdd(x4070,x4071)
    val x4074_x4073 = x4072 // FixConvert(x4072,TRUE,_64,_0)
    // x4074 = SimpleStruct(ArrayBuffer((offset,x4073), (size,Const(128)), (isLoad,Const(true))))
    val x4075 = OpDef(op=BitAnd, inputs=List(b2383, b2372)).name("x4075").ctrl(x4078) // And(b2383,b2372)
    val x4076 = OpDef(op=BitAnd, inputs=List(x4075, b2213)).name("x4076").ctrl(x4078) // And(x4075,b2213)
    val b4243_b4241 = WriteMem(b4241, x4074_x4073).name("b4243_b4241").ctrl(x4078) // StreamWrite(x4060,x4074,x4076)
    val b4244_b4242 = WriteMem(b4242, Const(128)).name("b4244_b4242").ctrl(x4078) // StreamWrite(x4060,x4074,x4076)
    val x4079 = FringeContainer(x3901,b4241,b4242,x4061).name("x4079").ctrl(x4089) // FringeDenseLoad(x3901,x4060,x4061)
    val x4080 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4080").ctrl(x4089) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4081 = CounterChain(List(x4080)).name("x4081").ctrl(x4089) // CounterChainNew(List(x4080))
    val x4088 = LoopController(style=InnerPipe, level=InnerControl, cchain=x4081).name("x4088").ctrl(x4089) // UnrolledForeach(List(b2383, b2372, b2213),x4081,Block(Const(())),List(List(b2406)),List(List(b2407)))
    val b2406 = CounterIter(x4080, None).ctrl(x4088).name("b2406")
    val b2407 = DummyOp().ctrl(x4088).name("b2407")
    val x4082 = OpDef(op=BitAnd, inputs=List(b2407, b2383)).name("x4082").ctrl(x4088) // And(b2407,b2383)
    val x4083 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4083").ctrl(x4088) // And(b2372,b2213)
    val x4084 = OpDef(op=BitAnd, inputs=List(x4082, x4083)).name("x4084").ctrl(x4088) // And(x4082,x4083)
    val x4085_x4085 = ReadMem(x4061).name("x4085").ctrl(x4088) // ParStreamRead(x4061,List(x4084))
    val x4086_x4086 = x4085_x4085 // x4086 = VectorApply(x4085,0)
    val x4087 = StoreBanks(List(x3908_d0_b0, x3908_d1_b0, x3908_d2_b0, x3908_d3_b0), List(b2382, b2406), x4086_x4086).name("x4087").ctrl(x4088) // ParSRAMStore(x3908,List(List(b2382, b2406)),List(x4086),List(x4084))
    val x4090 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4090").ctrl(x4191) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4091 = CounterChain(List(x4090)).name("x4091").ctrl(x4191) // CounterChainNew(List(x4090))
    val x4120 = LoopController(style=StreamPipe, level=OuterControl, cchain=x4091).name("x4120").ctrl(x4191) // UnrolledForeach(List(b2372, b2213),x4091,Block(Const(())),List(List(b2418)),List(List(b2419)))
    val b2418 = CounterIter(x4090, Some(0)).ctrl(x4120).name("b2418")
    val b2419 = DummyOp().ctrl(x4120).name("b2419")
    val b4245 = StreamOut(field="offset").name("b4245").ctrl(x4120) // x4092 = StreamOutNew(BurstCmdBus)
    val b4246 = StreamOut(field="size").name("b4246").ctrl(x4120) // x4092 = StreamOutNew(BurstCmdBus)
    val x4093 = StreamIn(field="data").name("x4093").ctrl(x4120) // x4093 = StreamInNew(BurstDataBus())
    val x4109 = UnitController(style=SeqPipe, level=InnerControl).name("x4109").ctrl(x4120) // UnitPipe(List(b2419, b2372, b2213),Block(x4108))
    val x4094 = ReadMem(x4051_d1).name("x4094").ctrl(x4109) // RegRead(x4051)
    val x4095 = OpDef(op=FixAdd, inputs=List(x4094, b2418)).name("x4095").ctrl(x4109) // FixAdd(x4094,b2418)
    val x4096 = x4095 // FixConvert(x4095,TRUE,_32,_0)
    val x4097 = OpDef(op=FixSla, inputs=List(x4096, Const(6))).name("x4097").ctrl(x4109) // FixLsh(x4096,Const(6))
    val x4098 = Const(0) // FixConvert(Const(0),TRUE,_32,_0)
    val x4099 = OpDef(op=FixAdd, inputs=List(x4097, x4098)).name("x4099").ctrl(x4109) // FixAdd(x4097,x4098)
    val x4100 = OpDef(op=FixSla, inputs=List(x4099, Const(2))).name("x4100").ctrl(x4109) // FixLsh(x4099,Const(2))
    val x4101 = x4100 // FixConvert(x4100,TRUE,_64,_0)
    val x4102 = top.argFringe.dramAddress(x3903).name("x4102").ctrl(x4109) // GetDRAMAddress(x3903)
    val x4103 = OpDef(op=FixAdd, inputs=List(x4101, x4102)).name("x4103").ctrl(x4109) // FixAdd(x4101,x4102)
    val x4105_x4104 = x4103 // FixConvert(x4103,TRUE,_64,_0)
    // x4105 = SimpleStruct(ArrayBuffer((offset,x4104), (size,Const(256)), (isLoad,Const(true))))
    val x4106 = OpDef(op=BitAnd, inputs=List(b2419, b2372)).name("x4106").ctrl(x4109) // And(b2419,b2372)
    val x4107 = OpDef(op=BitAnd, inputs=List(x4106, b2213)).name("x4107").ctrl(x4109) // And(x4106,b2213)
    val b4247_b4245 = WriteMem(b4245, x4105_x4104).name("b4247_b4245").ctrl(x4109) // StreamWrite(x4092,x4105,x4107)
    val b4248_b4246 = WriteMem(b4246, Const(256)).name("b4248_b4246").ctrl(x4109) // StreamWrite(x4092,x4105,x4107)
    val x4110 = FringeContainer(x3903,b4245,b4246,x4093).name("x4110").ctrl(x4120) // FringeDenseLoad(x3903,x4092,x4093)
    val x4111 = Counter(min=Const(0), max=Const(64), step=Const(1), par=1).name("x4111").ctrl(x4120) // CounterNew(Const(0),Const(64),Const(1),Const(1))
    val x4112 = CounterChain(List(x4111)).name("x4112").ctrl(x4120) // CounterChainNew(List(x4111))
    val x4119 = LoopController(style=InnerPipe, level=InnerControl, cchain=x4112).name("x4119").ctrl(x4120) // UnrolledForeach(List(b2419, b2372, b2213),x4112,Block(Const(())),List(List(b2441)),List(List(b2442)))
    val b2441 = CounterIter(x4111, None).ctrl(x4119).name("b2441")
    val b2442 = DummyOp().ctrl(x4119).name("b2442")
    val x4113 = OpDef(op=BitAnd, inputs=List(b2442, b2419)).name("x4113").ctrl(x4119) // And(b2442,b2419)
    val x4114 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4114").ctrl(x4119) // And(b2372,b2213)
    val x4115 = OpDef(op=BitAnd, inputs=List(x4113, x4114)).name("x4115").ctrl(x4119) // And(x4113,x4114)
    val x4116_x4116 = ReadMem(x4093).name("x4116").ctrl(x4119) // ParStreamRead(x4093,List(x4115))
    val x4117_x4117 = x4116_x4116 // x4117 = VectorApply(x4116,0)
    val x4118 = StoreBanks(List(x3907_d0_b0), List(b2418, b2441), x4117_x4117).name("x4118").ctrl(x4119) // ParSRAMStore(x3907,List(List(b2418, b2441)),List(x4117),List(x4115))
    val x4121 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4121").ctrl(x4191) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4122 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4122").ctrl(x4191) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4123 = CounterChain(List(x4122,x4121)).name("x4123").ctrl(x4191) // CounterChainNew(List(x4122, x4121))
    val x4152 = LoopController(style=SeqPipe, level=OuterControl, cchain=x4123).name("x4152").ctrl(x4191) // UnrolledForeach(List(b2372, b2213),x4123,Block(Const(())),List(List(b2454), List(b2455)),List(List(b2456), List(b2457)))
    val b2454 = CounterIter(x4122, Some(0)).ctrl(x4152).name("b2454")
    val b2456 = DummyOp().ctrl(x4152).name("b2456")
    val b2455 = CounterIter(x4121, Some(0)).ctrl(x4152).name("b2455")
    val b2457 = DummyOp().ctrl(x4152).name("b2457")
    val x4124_d0 = Reg(init=0).name("x4124_d0").ctrl(x4152) // x4124 = RegNew(Const(0))
    isAccum(x4124_d0) = false
    val x4124_d1 = Reg(init=0).name("x4124_d1").ctrl(x4152) // x4124 = RegNew(Const(0))
    isAccum(x4124_d1) = true
    val x4125 = Counter(min=Const(0), max=Const(64), step=Const(1), par=1).name("x4125").ctrl(x4152) // CounterNew(Const(0),Const(64),Const(1),Const(1))
    val x4126 = CounterChain(List(x4125)).name("x4126").ctrl(x4152) // CounterChainNew(List(x4125))
    val x4143 = LoopController(style=InnerPipe, level=InnerControl, cchain=x4126).name("x4143").ctrl(x4152) // UnrolledReduce(List(b2456, b2457, b2372, b2213),x4126,x4124,Block((x4124) => Const(())),List(List(b2461)),List(List(b2462)))
    val b2461 = CounterIter(x4125, None).ctrl(x4143).name("b2461")
    val b2462 = DummyOp().ctrl(x4143).name("b2462")
    val x4127 = OpDef(op=BitAnd, inputs=List(b2462, b2456)).name("x4127").ctrl(x4143) // And(b2462,b2456)
    val x4128 = OpDef(op=BitAnd, inputs=List(b2457, b2372)).name("x4128").ctrl(x4143) // And(b2457,b2372)
    val x4129 = OpDef(op=BitAnd, inputs=List(x4127, x4128)).name("x4129").ctrl(x4143) // And(x4127,x4128)
    val x4130 = OpDef(op=BitAnd, inputs=List(x4129, b2213)).name("x4130").ctrl(x4143) // And(x4129,b2213)
    val x4131 = LoadBanks(List(x3907_d0_b0), List(b2454, b2461)).name("x4131").ctrl(x4143) // ParSRAMLoad(x3907,List(List(b2454, b2461)),List(x4130))
    val x4132 = x4131 // x4132 = VectorApply(x4131,0)
    val x4133 = LoadBanks(List(x3906_d0_b0), List(b2455, b2461)).name("x4133").ctrl(x4143) // ParSRAMLoad(x3906,List(List(b2455, b2461)),List(x4130))
    val x4134 = x4133 // x4134 = VectorApply(x4133,0)
    val x4135 = OpDef(op=FixMul, inputs=List(x4132, x4134)).name("x4135").ctrl(x4143) // FixMul(x4132,x4134)
    val x4136 = ReadMem(x4124_d1).name("x4136").ctrl(x4143) // RegRead(x4124)
    val x4137 = OpDef(op=FixEql, inputs=List(b2461, Const(0))).name("x4137").ctrl(x4143) // FixEql(b2461,Const(0))
    val x4138 = ReduceAccumOp(op=FixAdd, input=x4135, accum=x4136).name("x4138").ctrl(x4143) // FixAdd(x4135,x4136)
    val x4139 = OpDef(op=BitAnd, inputs=List(b2456, b2457)).name("x4139").ctrl(x4143) // And(b2456,b2457)
    val x4140 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4140").ctrl(x4143) // And(b2372,b2213)
    val x4141 = OpDef(op=BitAnd, inputs=List(x4139, x4140)).name("x4141").ctrl(x4143) // And(x4139,x4140)
    val x4142_x4124_d0 = WriteMem(x4124_d0, x4138).name("x4142_x4124_d0").ctrl(x4143) // RegWrite(x4124,x4138,x4141)
    val x4142_x4124_d1 = WriteMem(x4124_d1, x4138).name("x4142_x4124_d1").ctrl(x4143) // RegWrite(x4124,x4138,x4141)
    val x4151 = UnitController(style=SeqPipe, level=InnerControl).name("x4151").ctrl(x4152) // UnitPipe(List(b2456, b2457, b2372, b2213),Block(Const(())))
    val x4144 = OpDef(op=BitAnd, inputs=List(b2456, b2457)).name("x4144").ctrl(x4151) // And(b2456,b2457)
    val x4145 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4145").ctrl(x4151) // And(b2372,b2213)
    val x4146 = OpDef(op=BitAnd, inputs=List(x4144, x4145)).name("x4146").ctrl(x4151) // And(x4144,x4145)
    val x4147 = LoadBanks(List(x3908_d1_b0), List(b2454, b2455)).name("x4147").ctrl(x4151) // SRAMLoad(x3908,ArrayBuffer(Const(32), Const(32)),List(b2454, b2455),Const(0),x4146)
    val x4148 = ReadMem(x4124_d0).name("x4148").ctrl(x4151) // RegRead(x4124)
    val x4149 = OpDef(op=FixAdd, inputs=List(x4147, x4148)).name("x4149").ctrl(x4151) // FixAdd(x4147,x4148)
    val x4150 = StoreBanks(List(x3908_d0_b0, x3908_d1_b0, x3908_d2_b0, x3908_d3_b0), List(b2454, b2455), x4149).name("x4150").ctrl(x4151) // SRAMStore(x3908,ArrayBuffer(Const(32), Const(32)),List(b2454, b2455),Const(0),x4149,x4146)
    val x4153 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4153").ctrl(x4191) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4154 = CounterChain(List(x4153)).name("x4154").ctrl(x4191) // CounterChainNew(List(x4153))
    val x4190 = LoopController(style=StreamPipe, level=OuterControl, cchain=x4154).name("x4190").ctrl(x4191) // UnrolledForeach(List(b2372, b2213),x4154,Block(Const(())),List(List(b2491)),List(List(b2492)))
    val b2491 = CounterIter(x4153, Some(0)).ctrl(x4190).name("b2491")
    val b2492 = DummyOp().ctrl(x4190).name("b2492")
    val b4249 = StreamOut(field="offset").name("b4249").ctrl(x4190) // x4155 = StreamOutNew(BurstCmdBus)
    val b4250 = StreamOut(field="size").name("b4250").ctrl(x4190) // x4155 = StreamOutNew(BurstCmdBus)
    val x4156 = StreamOut(field="data").name("x4156").ctrl(x4190) // x4156 = StreamOutNew(BurstFullDataBus())
    val x4157 = StreamIn(field="ack").name("x4157").ctrl(x4190) // x4157 = StreamInNew(BurstAckBus)
    val x4174 = UnitController(style=SeqPipe, level=InnerControl).name("x4174").ctrl(x4190) // UnitPipe(List(b2492, b2372, b2213),Block(x4173))
    val x4158 = ReadMem(x4051_d0).name("x4158").ctrl(x4174) // RegRead(x4051)
    val x4159 = OpDef(op=FixAdd, inputs=List(x4158, b2491)).name("x4159").ctrl(x4174) // FixAdd(x4158,b2491)
    val x4160 = x4159 // FixConvert(x4159,TRUE,_32,_0)
    val x4161 = ReadMem(x3897_d0).name("x4161").ctrl(x4174) // RegRead(x3897)
    val x4162 = OpDef(op=FixMul, inputs=List(x4160, x4161)).name("x4162").ctrl(x4174) // FixMul(x4160,x4161)
    val x4163 = b2212 // FixConvert(b2212,TRUE,_32,_0)
    val x4164 = OpDef(op=FixAdd, inputs=List(x4162, x4163)).name("x4164").ctrl(x4174) // FixAdd(x4162,x4163)
    val x4165 = OpDef(op=FixSla, inputs=List(x4164, Const(2))).name("x4165").ctrl(x4174) // FixLsh(x4164,Const(2))
    val x4166 = x4165 // FixConvert(x4165,TRUE,_64,_0)
    val x4167 = top.argFringe.dramAddress(x3901).name("x4167").ctrl(x4174) // GetDRAMAddress(x3901)
    val x4168 = OpDef(op=FixAdd, inputs=List(x4166, x4167)).name("x4168").ctrl(x4174) // FixAdd(x4166,x4167)
    val x4170_x4169 = x4168 // FixConvert(x4168,TRUE,_64,_0)
    // x4170 = SimpleStruct(ArrayBuffer((offset,x4169), (size,Const(128)), (isLoad,Const(false))))
    val x4171 = OpDef(op=BitAnd, inputs=List(b2492, b2372)).name("x4171").ctrl(x4174) // And(b2492,b2372)
    val x4172 = OpDef(op=BitAnd, inputs=List(x4171, b2213)).name("x4172").ctrl(x4174) // And(x4171,b2213)
    val b4251_b4249 = WriteMem(b4249, x4170_x4169).name("b4251_b4249").ctrl(x4174) // StreamWrite(x4155,x4170,x4172)
    val b4252_b4250 = WriteMem(b4250, Const(128)).name("b4252_b4250").ctrl(x4174) // StreamWrite(x4155,x4170,x4172)
    val x4175 = Counter(min=Const(0), max=Const(32), step=Const(1), par=1).name("x4175").ctrl(x4190) // CounterNew(Const(0),Const(32),Const(1),Const(1))
    val x4176 = CounterChain(List(x4175)).name("x4176").ctrl(x4190) // CounterChainNew(List(x4175))
    val x4184 = LoopController(style=InnerPipe, level=InnerControl, cchain=x4176).name("x4184").ctrl(x4190) // UnrolledForeach(List(b2492, b2372, b2213),x4176,Block(Const(())),List(List(b2515)),List(List(b2516)))
    val b2515 = CounterIter(x4175, None).ctrl(x4184).name("b2515")
    val b2516 = DummyOp().ctrl(x4184).name("b2516")
    val x4177 = OpDef(op=BitAnd, inputs=List(b2516, b2492)).name("x4177").ctrl(x4184) // And(b2516,b2492)
    val x4178 = OpDef(op=BitAnd, inputs=List(b2372, b2213)).name("x4178").ctrl(x4184) // And(b2372,b2213)
    val x4179 = OpDef(op=BitAnd, inputs=List(x4177, x4178)).name("x4179").ctrl(x4184) // And(x4177,x4178)
    val x4180 = LoadBanks(List(x3908_d0_b0), List(b2491, b2515)).name("x4180").ctrl(x4184) // ParSRAMLoad(x3908,List(List(b2491, b2515)),List(x4179))
    val x4182_x4181 = x4180 // x4181 = VectorApply(x4180,0)
    // x4182 = SimpleStruct(ArrayBuffer((_1,x4181), (_2,Const(true))))
    val x4183_x4156 = WriteMem(x4156, x4182_x4181).name("x4183_x4156").ctrl(x4184) // ParStreamWrite(x4156,List(x4182),List(x4179))
    val x4185 = FringeContainer(x3901,b4249,b4250,x4156,x4157).name("x4185").ctrl(x4190) // FringeDenseStore(x3901,x4155,x4156,x4157)
    val x4189 = UnitController(style=SeqPipe, level=InnerControl).name("x4189").ctrl(x4190) // UnitPipe(List(b2492, b2372, b2213),Block(Const(())))
    val x4186 = OpDef(op=BitAnd, inputs=List(b2492, b2372)).name("x4186").ctrl(x4189) // And(b2492,b2372)
    val x4187 = OpDef(op=BitAnd, inputs=List(x4186, b2213)).name("x4187").ctrl(x4189) // And(x4186,b2213)
    val x4188_x4188 = ReadMem(x4157).name("x4188").ctrl(x4189) // StreamRead(x4157,x4187)
    
  }
}