package prism

case class RunPass[P<:Pass:ClassTag](session:Session, id:Int) extends Serializable {
  val pct = implicitly[ClassTag[P]]
  var _pass:Option[P] = None
  def pass:Pass = _pass.get
  var name:String = _
  var status:RunStatus = RunPassNotRun
  def logFile = s"$name.log"

  def setPass(pass:Pass) = {
    pass match {
      case pass:P => this._pass = Some(pass)
      case _ => throw SessionRestoreFailure(s"Restored RunPass[${pct}] cannot load $pass") 
    }
    this.name = s"$pass-$id"
    dependencies.clear
  }

  def clearPass = {
    _pass = None
    dependencies.clear
  }

  def rerun:this.type = { status = RunPassNotRun; this }

  def hasRun = status != RunPassNotRun
  def succeeded = status == RunPassSucceeded
  def failed = status == RunPassFailed

  val dependencies = ListBuffer[RunPass[_]]()
  def dependsOn(deps:Pass*):this.type = {
    deps.foreach { dep =>
      dependencies += session.passes(dep).last //TODO: If dependency is not added, session.passes does not contain dep yet.
    }
    this
  }
  def unfinishedDependencies = dependencies.filter { !_.succeeded }
  def isDependencyFree = unfinishedDependencies.isEmpty

  def prevRuns:Iterable[RunPass[_]] = {
    session.runPasses.slice(0, id)
  }

  def run(implicit compiler:Compiler):Unit = {
    if (!pass.shouldRun) return
    if (hasRun) return
    if (!isDependencyFree) 
      err(s"Cannot run pass $name due to dependencies=${unfinishedDependencies.map(_.name).mkString(",")} haven't run")

    try {
      withLog(append=false) {
        pass.run(this)
      }
      status = RunPassSucceeded
    } catch {
      case e:Exception =>
        status = RunPassFailed
        throw e
    }
  }

  def withLog(append:Boolean)(lambda: => Unit)(implicit compiler:Compiler) {
    if (pass.logger.isOpen) lambda 
    else pass.logger.withOpen(logFile, append) { lambda }
  }
}

sealed trait RunStatus extends Serializable
case object RunPassNotRun extends RunStatus
case object RunPassSucceeded extends RunStatus
case object RunPassFailed extends RunStatus

case class SessionRestoreFailure(msg:String) extends PIRException