package ch.fhnw.ima.bimgur.model

import ch.fhnw.ima.bimgur.model.activiti.Analysis
import diode.data.Pot

case class BimgurModel(analyses: Pot[Seq[Analysis]])