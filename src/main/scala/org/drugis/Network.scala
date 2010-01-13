package org.drugis

class Network(_treatments: Set[Treatment], _studies: Set[Study]) {
	val treatments = _treatments
	val studies = _studies
	override def toString = treatments.toString + studies.toString

	val treatmentGraph: UndirectedGraph[Treatment] = {
		var graph = new UndirectedGraph[Treatment](
			Set[(Treatment, Treatment)]())
		for (s <- studies) {
			graph = graph.union(s.treatmentGraph)
		}
		graph
	}

	def supportingEvidence(cycle: UndirectedGraph[Treatment])
		: Set[UndirectedGraph[Treatment]] = {
		Set[UndirectedGraph[Treatment]]() ++ {
			for {s <- studies
				val edges = s.treatmentGraph.intersection(cycle).edgeSet
				if !edges.isEmpty
			} yield new UndirectedGraph[Treatment](edges)
		}
	}

	def evidenceMatrix(cycle: UndirectedGraph[Treatment])
	: Matrix[Boolean] = {
		new Matrix[Boolean](
			{for {s <- supportingEvidence(cycle)} 
			yield s.incidenceVector(cycle.edgeVector)}.toList)
	}

	def evidenceDimensionality(cycle: UndirectedGraph[Treatment]): Int = {
		val m = Matrix.gaussElimGF2(evidenceMatrix(cycle))
		val i = m.elements.findIndexOf(r => !r.contains(true))
		if (i == -1) m.nRows
		else i
	}

	def isInconsistency(cycle: UndirectedGraph[Treatment]): Boolean =
		evidenceDimensionality(cycle) >= 3

	val edgeVector: List[(Treatment, Treatment)] =
		treatmentGraph.edgeVector
}

object Network {
	def fromXML(node: scala.xml.Node): Network =  {
		val treatments = treatmentsFromXML((node \ "treatments")(0))
		new Network(Set[Treatment]() ++ treatments.values, 
			studiesFromXML((node \ "studies")(0), treatments))
	}

	def treatmentsFromXML(n: scala.xml.Node): Map[String, Treatment] =
		Map[String, Treatment]() ++
		{for (node <- n \ "treatment") yield ((node \ "@id").text, Treatment.fromXML(node))}

	def studiesFromXML(n: scala.xml.Node, treatments: Map[String, Treatment]): Set[Study] =
		Set[Study]() ++
		{for (node <- n \ "study") yield Study.fromXML(node, treatments)}
}
