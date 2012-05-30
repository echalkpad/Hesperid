////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.util;

import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ClientHierarchy;
import ch.astina.hesperid.model.base.ClientRelationType;
import ch.astina.hesperid.model.base.Location;
import ch.astina.hesperid.model.base.System;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
public class DotNotationBuilder
{
	public static String buildAssetGraph(List<Asset> assets, System mesSystem)
	{
		Map<String, Boolean> relations = new HashMap<String, Boolean>();
		Map<String, Boolean> plugs = new HashMap<String, Boolean>();
		
		String dotNotationGraph = "graph{";
		
		for (Asset asset : assets) {
			if (asset.getSystem() != null && asset.getSystem().equals(mesSystem)) {
				dotNotationGraph += buildNodeStatement(asset, plugs) + ";";
				
				for (ClientHierarchy clientHierarchy : asset.getClientHierarchies()) {
					if (clientHierarchy.getSecondAsset().getSystem() != null
							&& clientHierarchy.getSecondAsset().getSystem().equals(mesSystem)) {
						if (!relations.containsKey(asset.getAssetIdentifier() + "-"
								+ clientHierarchy.getSecondAsset().getAssetIdentifier())
								&& !relations.containsKey(clientHierarchy.getSecondAsset()
										.getAssetIdentifier() + "-" + asset.getAssetIdentifier())) {
							dotNotationGraph += buildEdgeStatement(asset,
									clientHierarchy.getSecondAsset(),
									clientHierarchy.getClientRelationType(), plugs)
									+ ";";
							relations.put(asset.getAssetIdentifier() + "-"
									+ clientHierarchy.getSecondAsset().getAssetIdentifier(),
									Boolean.TRUE);
						}
					}
				}
			}
		}
		
		dotNotationGraph += "}";
		
		return dotNotationGraph;
	}
	
	public static String buildAssetGraph(List<Asset> assets, Location manufacturingLocation)
	{
		Map<String, Boolean> relations = new HashMap<String, Boolean>();
		Map<String, Boolean> plugs = new HashMap<String, Boolean>();
		
		String dotNotationGraph = "graph{";
		
		for (Asset asset : assets) {
			if (asset.getLocation() != null
					&& asset.getLocation().equals(manufacturingLocation)) {
				dotNotationGraph += buildNodeStatement(asset, plugs) + ";";
				
				for (ClientHierarchy clientHierarchy : asset.getClientHierarchies()) {
					if (clientHierarchy.getSecondAsset().getLocation() != null
							&& clientHierarchy.getSecondAsset().getLocation()
									.equals(manufacturingLocation)) {
						if (!relations.containsKey(asset.getAssetIdentifier() + "-"
								+ clientHierarchy.getSecondAsset().getAssetIdentifier())
								&& !relations.containsKey(clientHierarchy.getSecondAsset()
										.getAssetIdentifier() + "-" + asset.getAssetIdentifier())) {
							dotNotationGraph += buildEdgeStatement(asset,
									clientHierarchy.getSecondAsset(),
									clientHierarchy.getClientRelationType(), plugs)
									+ ";";
							relations.put(asset.getAssetIdentifier() + "-"
									+ clientHierarchy.getSecondAsset().getAssetIdentifier(),
									Boolean.TRUE);
						}
					}
				}
			}
		}
		
		dotNotationGraph += "}";
		
		return dotNotationGraph;
	}
	
	public static String buildAssetGraph(List<Asset> assets)
	{
		Map<String, Boolean> relations = new HashMap<String, Boolean>();
		Map<String, Boolean> plugs = new HashMap<String, Boolean>();
		
		String dotNotationGraph = "graph{";
		
		for (Asset asset : assets) {
			dotNotationGraph += buildNodeStatement(asset, plugs) + ";";
			
			for (ClientHierarchy clientHierarchy : asset.getClientHierarchies()) {
				if (!relations.containsKey(asset.getAssetIdentifier() + "-"
						+ clientHierarchy.getSecondAsset().getAssetIdentifier())
						&& !relations.containsKey(clientHierarchy.getSecondAsset()
								.getAssetIdentifier() + "-" + asset.getAssetIdentifier())) {
					dotNotationGraph += buildEdgeStatement(asset, clientHierarchy.getSecondAsset(),
							clientHierarchy.getClientRelationType(), plugs) + ";";
					relations.put(asset.getAssetIdentifier() + "-"
							+ clientHierarchy.getSecondAsset().getAssetIdentifier(), Boolean.TRUE);
				}
			}
		}
		
		dotNotationGraph += "}";
		
		return dotNotationGraph;
	}
	
	public static String buildNodeStatement(Asset asset, Map<String, Boolean> plugs)
	{
		String nodeStatement = "";
		
		String plug = "";
		
		if (asset.getErrorOccured() && !plugs.containsKey(asset.getAssetIdentifier())) {
			plug = "\"" + asset.getAssetIdentifier() + "\"[color=red];";
			plugs.put(asset.getAssetIdentifier(), Boolean.TRUE);
		}
		
		nodeStatement = plug + "\"" + asset.getAssetIdentifier() + "\"";
		
		return nodeStatement;
	}
	
	public static String buildEdgeStatement(Asset asset, Asset relatedAsset,
			ClientRelationType clientRelationType, Map<String, Boolean> plugs)
	{
		String edgeStatement = "";
		
		edgeStatement = buildNodeStatement(asset, plugs) + "--"
				+ buildNodeStatement(relatedAsset, plugs);
		
		if (clientRelationType == ClientRelationType.DEPENDS_ON) {
			edgeStatement += "[arrowhead=diamond]";
		}
		
		return edgeStatement;
	}
}
