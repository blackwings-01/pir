import pir._
import pir.node._
import arch._
import pirc.enums._

object TPCHQ6 extends PIRApp {
  def main(top:Top) = {
    val discts_oc = OffChip("discts")
    val x2982_x2992_data_v = Vector("x2982_x2992_data")
    val bus_379_v = Vector("bus_379")
    val x2899_b3140_x2908_b3142_s = Scalar("x2899_b3140_x2908_b3142")
    val x3021_b3164_x3030_b3166_s = Scalar("x3021_b3164_x3030_b3166")
    val bus_373_v = Vector("bus_373")
    val bus_397_v = Vector("bus_397")
    val x2869_x2869_dsp0_bank0_data_v = Vector("x2869_x2869_dsp0_bank0_data")
    val x3042_x3095_s = Scalar("x3042_x3095")
    val x2919_b3143_x2928_b3145_s = Scalar("x2919_b3143_x2928_b3145")
    val dataSize_argin = ArgIn("dataSize")
    val x2875_x2875_dsp0_bank0_data_v = Vector("x2875_x2875_dsp0_bank0_data")
    val x2871_x2871_dsp0_bank0_data_v = Vector("x2871_x2871_dsp0_bank0_data")
    val x2981_b3156_x2990_b3158_s = Scalar("x2981_b3156_x2990_b3158")
    val x2981_b3155_x2990_b3157_s = Scalar("x2981_b3155_x2990_b3157")
    val prices_da = DRAMAddress("prices", "prices")
    val bus_391_v = Vector("bus_391")
    val x2900_x2910_data_v = Vector("x2900_x2910_data")
    val x2939_b3147_x2948_b3149_s = Scalar("x2939_b3147_x2948_b3149")
    val x2874_x2874_dsp0_bank0_data_v = Vector("x2874_x2874_dsp0_bank0_data")
    val dates_da = DRAMAddress("dates", "dates")
    val x2961_b3152_x2970_b3154_s = Scalar("x2961_b3152_x2970_b3154")
    val x3001_b3159_x3010_b3161_s = Scalar("x3001_b3159_x3010_b3161")
    val dates_oc = OffChip("dates")
    val x2962_x2972_data_v = Vector("x2962_x2972_data")
    val x2899_b3139_x2908_b3141_s = Scalar("x2899_b3139_x2908_b3141")
    val quants_da = DRAMAddress("quants", "quants")
    val x2879_b3135_x2888_b3137_s = Scalar("x2879_b3135_x2888_b3137")
    val x2872_x2872_dsp0_bank0_data_v = Vector("x2872_x2872_dsp0_bank0_data")
    val x2940_x2950_data_v = Vector("x2940_x2950_data")
    val discts_da = DRAMAddress("discts", "discts")
    val x2919_b3144_x2928_b3146_s = Scalar("x2919_b3144_x2928_b3146")
    val x2879_b3136_x2888_b3138_s = Scalar("x2879_b3136_x2888_b3138")
    val x2876_x2876_dsp0_bank0_data_v = Vector("x2876_x2876_dsp0_bank0_data")
    val x2920_x2930_data_v = Vector("x2920_x2930_data")
    val x2880_x2890_data_v = Vector("x2880_x2890_data")
    val prices_oc = OffChip("prices")
    val x3022_x3032_data_v = Vector("x3022_x3032_data")
    val x2873_x2873_dsp0_bank0_data_v = Vector("x2873_x2873_dsp0_bank0_data")
    val x3041_x3068_s = Scalar("x3041_x3068")
    val x3001_b3160_x3010_b3162_s = Scalar("x3001_b3160_x3010_b3162")
    val quants_oc = OffChip("quants")
    val x2961_b3151_x2970_b3153_s = Scalar("x2961_b3151_x2970_b3153")
    val x2939_b3148_x2948_b3150_s = Scalar("x2939_b3148_x2948_b3150")
    val x3002_x3012_data_v = Vector("x3002_x3012_data")
    val x3021_b3163_x3030_b3165_s = Scalar("x3021_b3163_x3030_b3165")
    val x2870_x2870_dsp0_bank0_data_v = Vector("x2870_x2870_dsp0_bank0_data")
    val x2860_x3109_argout = ArgOut("x2860_x3109")
    val x3107 = MetaPipeline(name="x3107",parent="top") { implicit CU => 
      val x2850 = ScalarBuffer(name="x2850")
        .buffering(1)
        .store(dataSize_argin, None, None)
      val ctr1 = Counter(min=Const(0), max=x2850.readPort, step=Const(384), par=2) // Counter
      val x2868 = CounterChain(name = "x2868", ctr1).iter(1)
    }
    val x2869_dsp0_bank0 = MemoryPipeline(name="x2869_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2896 = VectorFIFO(size=1,name="x2896")
        .store(x2880_x2890_data_v, None, None)
      val x2892 = CounterChain.copy("x2897", "x2892")
      val x3044 = CounterChain.copy("x3069", "x3044")
      val x2869 = SRAM(size=24,name="x2869",banking = Strided(1,16))
        .buffering(2)
        .store(x2896.readPort, Some(x2892(0)), Some("x2898"))
        .load(x2869_x2869_dsp0_bank0_data_v, Some(x3044(0)), Some("x3069"))
    }
    val x2870_dsp0_bank0 = MemoryPipeline(name="x2870_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2978 = VectorFIFO(size=1,name="x2978")
        .store(x2962_x2972_data_v, None, None)
      val x2974 = CounterChain.copy("x2979", "x2974")
      val x3071 = CounterChain.copy("x3096", "x3071")
      val x2870 = SRAM(size=24,name="x2870",banking = Strided(1,16))
        .buffering(2)
        .store(x2978.readPort, Some(x2974(0)), Some("x2980"))
        .load(x2870_x2870_dsp0_bank0_data_v, Some(x3071(0)), Some("x3096"))
    }
    val x2871_dsp0_bank0 = MemoryPipeline(name="x2871_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2916 = VectorFIFO(size=1,name="x2916")
        .store(x2900_x2910_data_v, None, None)
      val x2912 = CounterChain.copy("x2917", "x2912")
      val x3044 = CounterChain.copy("x3069", "x3044")
      val x2871 = SRAM(size=24,name="x2871",banking = Strided(1,16))
        .buffering(2)
        .store(x2916.readPort, Some(x2912(0)), Some("x2918"))
        .load(x2871_x2871_dsp0_bank0_data_v, Some(x3044(0)), Some("x3069"))
    }
    val x2872_dsp0_bank0 = MemoryPipeline(name="x2872_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2998 = VectorFIFO(size=1,name="x2998")
        .store(x2982_x2992_data_v, None, None)
      val x2994 = CounterChain.copy("x2999", "x2994")
      val x3071 = CounterChain.copy("x3096", "x3071")
      val x2872 = SRAM(size=24,name="x2872",banking = Strided(1,16))
        .buffering(2)
        .store(x2998.readPort, Some(x2994(0)), Some("x3000"))
        .load(x2872_x2872_dsp0_bank0_data_v, Some(x3071(0)), Some("x3096"))
    }
    val x2873_dsp0_bank0 = MemoryPipeline(name="x2873_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2936 = VectorFIFO(size=1,name="x2936")
        .store(x2920_x2930_data_v, None, None)
      val x2932 = CounterChain.copy("x2937", "x2932")
      val x3044 = CounterChain.copy("x3069", "x3044")
      val x2873 = SRAM(size=24,name="x2873",banking = Strided(1,16))
        .buffering(2)
        .store(x2936.readPort, Some(x2932(0)), Some("x2938"))
        .load(x2873_x2873_dsp0_bank0_data_v, Some(x3044(0)), Some("x3069"))
    }
    val x2874_dsp0_bank0 = MemoryPipeline(name="x2874_dsp0_bank0",parent="x3107") { implicit CU => 
      val x3018 = VectorFIFO(size=1,name="x3018")
        .store(x3002_x3012_data_v, None, None)
      val x3014 = CounterChain.copy("x3019", "x3014")
      val x3071 = CounterChain.copy("x3096", "x3071")
      val x2874 = SRAM(size=24,name="x2874",banking = Strided(1,16))
        .buffering(2)
        .store(x3018.readPort, Some(x3014(0)), Some("x3020"))
        .load(x2874_x2874_dsp0_bank0_data_v, Some(x3071(0)), Some("x3096"))
    }
    val x2875_dsp0_bank0 = MemoryPipeline(name="x2875_dsp0_bank0",parent="x3107") { implicit CU => 
      val x2956 = VectorFIFO(size=1,name="x2956")
        .store(x2940_x2950_data_v, None, None)
      val x2952 = CounterChain.copy("x2957", "x2952")
      val x3044 = CounterChain.copy("x3069", "x3044")
      val x2875 = SRAM(size=24,name="x2875",banking = Strided(1,16))
        .buffering(2)
        .store(x2956.readPort, Some(x2952(0)), Some("x2958"))
        .load(x2875_x2875_dsp0_bank0_data_v, Some(x3044(0)), Some("x3069"))
    }
    val x2876_dsp0_bank0 = MemoryPipeline(name="x2876_dsp0_bank0",parent="x3107") { implicit CU => 
      val x3038 = VectorFIFO(size=1,name="x3038")
        .store(x3022_x3032_data_v, None, None)
      val x3034 = CounterChain.copy("x3039", "x3034")
      val x3071 = CounterChain.copy("x3096", "x3071")
      val x2876 = SRAM(size=24,name="x2876",banking = Strided(1,16))
        .buffering(2)
        .store(x3038.readPort, Some(x3034(0)), Some("x3040"))
        .load(x2876_x2876_dsp0_bank0_data_v, Some(x3071(0)), Some("x3096"))
    }
    val x2898 = StreamController(name="x2898",parent="x3107") { implicit CU => 
      val x2898_unit = CounterChain(name = "x2898_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2889_0 = Pipeline(name="x2889_0",parent="x2898") { implicit CU => 
      val x2882 = CU.temp(None).name("x2882")
      val x2884 = ScalarBuffer(name="x2884")
        .buffering(1)
        .store(dates_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 0)
      val x2889_unit = CounterChain(name = "x2889_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2882))
      Stage(operands=List(x2882, x2884), op=FixAdd, results=List(CU.scalarOut(x2879_b3135_x2888_b3137_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2879_b3136_x2888_b3138_s)))
    }
    val x2890 = MemoryController(name="x2890",parent="x2898",offchip=dates_oc, mctpe=TileLoad) { implicit CU => 
      val x2879_b3136 = ScalarFIFO(size=1,name="size")
        .store(x2879_b3136_x2888_b3138_s, None, None)
      val x2879_b3135 = ScalarFIFO(size=1,name="offset")
        .store(x2879_b3135_x2888_b3137_s, None, None)
      CU.newOut("data", x2880_x2890_data_v)
    }
    val x2897 = Pipeline(name="x2897",parent="x2898") { implicit CU => 
      val ctr2 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2892 = CounterChain(name = "x2892", ctr2).iter(24)
    }
    val x2918 = StreamController(name="x2918",parent="x3107") { implicit CU => 
      val x2918_unit = CounterChain(name = "x2918_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2909_0 = Pipeline(name="x2909_0",parent="x2918") { implicit CU => 
      val x2902 = CU.temp(None).name("x2902")
      val x2904 = ScalarBuffer(name="x2904")
        .buffering(1)
        .store(quants_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 0)
      val x2909_unit = CounterChain(name = "x2909_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2902))
      Stage(operands=List(x2902, x2904), op=FixAdd, results=List(CU.scalarOut(x2899_b3139_x2908_b3141_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2899_b3140_x2908_b3142_s)))
    }
    val x2910 = MemoryController(name="x2910",parent="x2918",offchip=quants_oc, mctpe=TileLoad) { implicit CU => 
      val x2899_b3139 = ScalarFIFO(size=1,name="offset")
        .store(x2899_b3139_x2908_b3141_s, None, None)
      val x2899_b3140 = ScalarFIFO(size=1,name="size")
        .store(x2899_b3140_x2908_b3142_s, None, None)
      CU.newOut("data", x2900_x2910_data_v)
    }
    val x2917 = Pipeline(name="x2917",parent="x2918") { implicit CU => 
      val ctr3 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2912 = CounterChain(name = "x2912", ctr3).iter(24)
    }
    val x2938 = StreamController(name="x2938",parent="x3107") { implicit CU => 
      val x2938_unit = CounterChain(name = "x2938_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2929_0 = Pipeline(name="x2929_0",parent="x2938") { implicit CU => 
      val x2922 = CU.temp(None).name("x2922")
      val x2924 = ScalarBuffer(name="x2924")
        .buffering(1)
        .store(discts_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 0)
      val x2929_unit = CounterChain(name = "x2929_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2922))
      Stage(operands=List(x2922, x2924), op=FixAdd, results=List(CU.scalarOut(x2919_b3143_x2928_b3145_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2919_b3144_x2928_b3146_s)))
    }
    val x2930 = MemoryController(name="x2930",parent="x2938",offchip=discts_oc, mctpe=TileLoad) { implicit CU => 
      val x2919_b3144 = ScalarFIFO(size=1,name="size")
        .store(x2919_b3144_x2928_b3146_s, None, None)
      val x2919_b3143 = ScalarFIFO(size=1,name="offset")
        .store(x2919_b3143_x2928_b3145_s, None, None)
      CU.newOut("data", x2920_x2930_data_v)
    }
    val x2937 = Pipeline(name="x2937",parent="x2938") { implicit CU => 
      val ctr4 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2932 = CounterChain(name = "x2932", ctr4).iter(24)
    }
    val x2958 = StreamController(name="x2958",parent="x3107") { implicit CU => 
      val x2958_unit = CounterChain(name = "x2958_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2949_0 = Pipeline(name="x2949_0",parent="x2958") { implicit CU => 
      val x2942 = CU.temp(None).name("x2942")
      val x2944 = ScalarBuffer(name="x2944")
        .buffering(1)
        .store(prices_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 0)
      val x2949_unit = CounterChain(name = "x2949_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2942))
      Stage(operands=List(x2942, x2944), op=FixAdd, results=List(CU.scalarOut(x2939_b3147_x2948_b3149_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2939_b3148_x2948_b3150_s)))
    }
    val x2950 = MemoryController(name="x2950",parent="x2958",offchip=prices_oc, mctpe=TileLoad) { implicit CU => 
      val x2939_b3148 = ScalarFIFO(size=1,name="size")
        .store(x2939_b3148_x2948_b3150_s, None, None)
      val x2939_b3147 = ScalarFIFO(size=1,name="offset")
        .store(x2939_b3147_x2948_b3149_s, None, None)
      CU.newOut("data", x2940_x2950_data_v)
    }
    val x2957 = Pipeline(name="x2957",parent="x2958") { implicit CU => 
      val ctr5 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2952 = CounterChain(name = "x2952", ctr5).iter(24)
    }
    val x2980 = StreamController(name="x2980",parent="x3107") { implicit CU => 
      val x2980_unit = CounterChain(name = "x2980_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2971_0 = Pipeline(name="x2971_0",parent="x2980") { implicit CU => 
      val x2964 = CU.temp(None).name("x2964")
      val x2966 = ScalarBuffer(name="x2966")
        .buffering(1)
        .store(dates_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 1)
      val x2971_unit = CounterChain(name = "x2971_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2964))
      Stage(operands=List(x2964, x2966), op=FixAdd, results=List(CU.scalarOut(x2961_b3151_x2970_b3153_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2961_b3152_x2970_b3154_s)))
    }
    val x2972 = MemoryController(name="x2972",parent="x2980",offchip=dates_oc, mctpe=TileLoad) { implicit CU => 
      val x2961_b3151 = ScalarFIFO(size=1,name="offset")
        .store(x2961_b3151_x2970_b3153_s, None, None)
      val x2961_b3152 = ScalarFIFO(size=1,name="size")
        .store(x2961_b3152_x2970_b3154_s, None, None)
      CU.newOut("data", x2962_x2972_data_v)
    }
    val x2979 = Pipeline(name="x2979",parent="x2980") { implicit CU => 
      val ctr6 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2974 = CounterChain(name = "x2974", ctr6).iter(24)
    }
    val x3000 = StreamController(name="x3000",parent="x3107") { implicit CU => 
      val x3000_unit = CounterChain(name = "x3000_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x2991_0 = Pipeline(name="x2991_0",parent="x3000") { implicit CU => 
      val x2984 = CU.temp(None).name("x2984")
      val x2986 = ScalarBuffer(name="x2986")
        .buffering(1)
        .store(quants_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 1)
      val x2991_unit = CounterChain(name = "x2991_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x2984))
      Stage(operands=List(x2984, x2986), op=FixAdd, results=List(CU.scalarOut(x2981_b3155_x2990_b3157_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x2981_b3156_x2990_b3158_s)))
    }
    val x2992 = MemoryController(name="x2992",parent="x3000",offchip=quants_oc, mctpe=TileLoad) { implicit CU => 
      val x2981_b3156 = ScalarFIFO(size=1,name="size")
        .store(x2981_b3156_x2990_b3158_s, None, None)
      val x2981_b3155 = ScalarFIFO(size=1,name="offset")
        .store(x2981_b3155_x2990_b3157_s, None, None)
      CU.newOut("data", x2982_x2992_data_v)
    }
    val x2999 = Pipeline(name="x2999",parent="x3000") { implicit CU => 
      val ctr7 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x2994 = CounterChain(name = "x2994", ctr7).iter(24)
    }
    val x3020 = StreamController(name="x3020",parent="x3107") { implicit CU => 
      val x3020_unit = CounterChain(name = "x3020_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x3011_0 = Pipeline(name="x3011_0",parent="x3020") { implicit CU => 
      val x3004 = CU.temp(None).name("x3004")
      val x3006 = ScalarBuffer(name="x3006")
        .buffering(1)
        .store(discts_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 1)
      val x3011_unit = CounterChain(name = "x3011_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x3004))
      Stage(operands=List(x3004, x3006), op=FixAdd, results=List(CU.scalarOut(x3001_b3159_x3010_b3161_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x3001_b3160_x3010_b3162_s)))
    }
    val x3012 = MemoryController(name="x3012",parent="x3020",offchip=discts_oc, mctpe=TileLoad) { implicit CU => 
      val x3001_b3160 = ScalarFIFO(size=1,name="size")
        .store(x3001_b3160_x3010_b3162_s, None, None)
      val x3001_b3159 = ScalarFIFO(size=1,name="offset")
        .store(x3001_b3159_x3010_b3161_s, None, None)
      CU.newOut("data", x3002_x3012_data_v)
    }
    val x3019 = Pipeline(name="x3019",parent="x3020") { implicit CU => 
      val ctr8 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x3014 = CounterChain(name = "x3014", ctr8).iter(24)
    }
    val x3040 = StreamController(name="x3040",parent="x3107") { implicit CU => 
      val x3040_unit = CounterChain(name = "x3040_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
    }
    val x3031_0 = Pipeline(name="x3031_0",parent="x3040") { implicit CU => 
      val x3024 = CU.temp(None).name("x3024")
      val x3026 = ScalarBuffer(name="x3026")
        .buffering(1)
        .store(prices_da, None, None)
      val x2868 = CounterChain.copy("x3107", "x2868").iterIdx(0, 1)
      val x3031_unit = CounterChain(name = "x3031_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x2868(0), Const(2)), op=FixSla, results=List(x3024))
      Stage(operands=List(x3024, x3026), op=FixAdd, results=List(CU.scalarOut(x3021_b3163_x3030_b3165_s)))
      Stage(operands=List(Const(1536)), op=Bypass, results=List(CU.scalarOut(x3021_b3164_x3030_b3166_s)))
    }
    val x3032 = MemoryController(name="x3032",parent="x3040",offchip=prices_oc, mctpe=TileLoad) { implicit CU => 
      val x3021_b3163 = ScalarFIFO(size=1,name="offset")
        .store(x3021_b3163_x3030_b3165_s, None, None)
      val x3021_b3164 = ScalarFIFO(size=1,name="size")
        .store(x3021_b3164_x3030_b3166_s, None, None)
      CU.newOut("data", x3022_x3032_data_v)
    }
    val x3039 = Pipeline(name="x3039",parent="x3040") { implicit CU => 
      val ctr9 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x3034 = CounterChain(name = "x3034", ctr9).iter(24)
    }
    val x3069 = StreamController(name="x3069",parent="x3107") { implicit CU => 
      val ctr10 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x3044 = CounterChain(name = "x3044", ctr10).iter(24)
    }
    val x3069_2 = Pipeline(name="x3069_2",parent="x3069") { implicit CU => 
      val rr379 = VectorFIFO(size=1,name="rr379")
        .store(bus_379_v, None, None)
      val ctr14 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3069_2_unit = CounterChain(name = "x3069_2_unit", ctr14).iter(0)
      Stage(operands=List(rr379), op=Bypass, results=List(CU.reduce))
      val (_, rr381) = Stage.reduce(op=FixAdd, init=Const(0), accumParent="x3069")
      Stage(operands=List(rr381), op=Bypass, results=List(CU.scalarOut(x3041_x3068_s)))
    }
    val x3069_0 = Pipeline(name="x3069_0",parent="x3069") { implicit CU => 
      val x3054 = CU.temp(None).name("x3054")
      val x3058 = CU.temp(None).name("x3058")
      val x3057 = CU.temp(None).name("x3057")
      val x3059 = CU.temp(None).name("x3059")
      val x3056 = CU.temp(None).name("x3056")
      val x3055 = CU.temp(None).name("x3055")
      val x3046 = VectorFIFO(size=1,name="x3046")
        .store(x2869_x2869_dsp0_bank0_data_v, None, None)
      val x3048 = VectorFIFO(size=1,name="x3048")
        .store(x2873_x2873_dsp0_bank0_data_v, None, None)
      val ctr12 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3069_0_unit = CounterChain(name = "x3069_0_unit", ctr12).iter(0)
      Stage(operands=List(Const(0), x3046), op=FixLt, results=List(x3054))
      Stage(operands=List(x3046, Const(9999)), op=FixLt, results=List(x3055))
      Stage(operands=List(x3054, x3055), op=BitAnd, results=List(x3056))
      Stage(operands=List(Const(0), x3048), op=FixLeq, results=List(x3057))
      Stage(operands=List(x3056, x3057), op=BitAnd, results=List(x3058))
      Stage(operands=List(x3048, Const(9999)), op=FixLeq, results=List(x3059))
      Stage(operands=List(x3058, x3059), op=BitAnd, results=List(CU.vecOut(bus_373_v)))
    }
    val x3069_1 = Pipeline(name="x3069_1",parent="x3069") { implicit CU => 
      val x3062 = CU.temp(None).name("x3062")
      val x3063 = CU.temp(None).name("x3063")
      val x3061 = CU.temp(None).name("x3061")
      val x3060 = VectorFIFO(size=1,name="x3060")
        .store(bus_373_v, None, None)
      val x3052 = VectorFIFO(size=1,name="x3052")
        .store(x2875_x2875_dsp0_bank0_data_v, None, None)
      val x3048 = VectorFIFO(size=1,name="x3048")
        .store(x2873_x2873_dsp0_bank0_data_v, None, None)
      val x3050 = VectorFIFO(size=1,name="x3050")
        .store(x2871_x2871_dsp0_bank0_data_v, None, None)
      val ctr13 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3069_1_unit = CounterChain(name = "x3069_1_unit", ctr13).iter(0)
      Stage(operands=List(x3050, Const(24)), op=FixLt, results=List(x3061))
      Stage(operands=List(x3060, x3061), op=BitAnd, results=List(x3062))
      Stage(operands=List(x3052, x3048), op=FixMul, results=List(x3063))
      Stage(operands=List(x3062, x3063, Const(0)), op=MuxOp, results=List(CU.vecOut(bus_379_v)))
    }
    val x3096_2 = Pipeline(name="x3096_2",parent="x3096") { implicit CU => 
      val rr397 = VectorFIFO(size=1,name="rr397")
        .store(bus_397_v, None, None)
      val ctr17 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3096_2_unit = CounterChain(name = "x3096_2_unit", ctr17).iter(0)
      Stage(operands=List(rr397), op=Bypass, results=List(CU.reduce))
      val (_, rr399) = Stage.reduce(op=FixAdd, init=Const(0), accumParent="x3096")
      Stage(operands=List(rr399), op=Bypass, results=List(CU.scalarOut(x3042_x3095_s)))
    }
    val x3096 = StreamController(name="x3096",parent="x3107") { implicit CU => 
      val ctr11 = Counter(min=Const(0), max=Const(384), step=Const(1), par=16) // Counter
      val x3071 = CounterChain(name = "x3071", ctr11).iter(24)
    }
    val x3096_0 = Pipeline(name="x3096_0",parent="x3096") { implicit CU => 
      val x3086 = CU.temp(None).name("x3086")
      val x3083 = CU.temp(None).name("x3083")
      val x3084 = CU.temp(None).name("x3084")
      val x3081 = CU.temp(None).name("x3081")
      val x3082 = CU.temp(None).name("x3082")
      val x3085 = CU.temp(None).name("x3085")
      val x3073 = VectorFIFO(size=1,name="x3073")
        .store(x2870_x2870_dsp0_bank0_data_v, None, None)
      val x3075 = VectorFIFO(size=1,name="x3075")
        .store(x2874_x2874_dsp0_bank0_data_v, None, None)
      val ctr15 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3096_0_unit = CounterChain(name = "x3096_0_unit", ctr15).iter(0)
      Stage(operands=List(Const(0), x3073), op=FixLt, results=List(x3081))
      Stage(operands=List(x3073, Const(9999)), op=FixLt, results=List(x3082))
      Stage(operands=List(x3081, x3082), op=BitAnd, results=List(x3083))
      Stage(operands=List(Const(0), x3075), op=FixLeq, results=List(x3084))
      Stage(operands=List(x3083, x3084), op=BitAnd, results=List(x3085))
      Stage(operands=List(x3075, Const(9999)), op=FixLeq, results=List(x3086))
      Stage(operands=List(x3085, x3086), op=BitAnd, results=List(CU.vecOut(bus_391_v)))
    }
    val x3096_1 = Pipeline(name="x3096_1",parent="x3096") { implicit CU => 
      val x3089 = CU.temp(None).name("x3089")
      val x3090 = CU.temp(None).name("x3090")
      val x3088 = CU.temp(None).name("x3088")
      val x3087 = VectorFIFO(size=1,name="x3087")
        .store(bus_391_v, None, None)
      val x3079 = VectorFIFO(size=1,name="x3079")
        .store(x2876_x2876_dsp0_bank0_data_v, None, None)
      val x3075 = VectorFIFO(size=1,name="x3075")
        .store(x2874_x2874_dsp0_bank0_data_v, None, None)
      val x3077 = VectorFIFO(size=1,name="x3077")
        .store(x2872_x2872_dsp0_bank0_data_v, None, None)
      val ctr16 = Counter(min=Const(0), max=Const(1), step=Const(16), par=16) // Counter
      val x3096_1_unit = CounterChain(name = "x3096_1_unit", ctr16).iter(0)
      Stage(operands=List(x3077, Const(24)), op=FixLt, results=List(x3088))
      Stage(operands=List(x3087, x3088), op=BitAnd, results=List(x3089))
      Stage(operands=List(x3079, x3075), op=FixMul, results=List(x3090))
      Stage(operands=List(x3089, x3090, Const(0)), op=MuxOp, results=List(CU.vecOut(bus_397_v)))
    }
    val x3106_0 = Pipeline(name="x3106_0",parent="x3107") { implicit CU => 
      val x3042 = ScalarBuffer(name="x3042")
        .buffering(2)
        .store(x3042_x3095_s, None, Some("x3096"))
      val x3041 = ScalarBuffer(name="x3041")
        .buffering(2)
        .store(x3041_x3068_s, None, Some("x3069"))
      val x3106_unit = CounterChain(name = "x3106_unit", Counter(Const(0), Const(1), Const(1), par=1)).iter(1l)
      Stage(operands=List(x3041, x3042), op=FixAdd, results=List(CU.reduce))
      val (_, rr404) = Stage.reduce(op=FixAdd, init=Const(0), accumParent="x3107")
      Stage(operands=List(rr404), op=Bypass, results=List(CU.scalarOut(x2860_x3109_argout)))
    }
    
  }
}