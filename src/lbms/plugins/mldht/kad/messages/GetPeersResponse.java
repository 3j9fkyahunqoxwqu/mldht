/*
 *    This file is part of mlDHT.
 * 
 *    mlDHT is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 2 of the License, or
 *    (at your option) any later version.
 * 
 *    mlDHT is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with mlDHT.  If not, see <http://www.gnu.org/licenses/>.
 */
package lbms.plugins.mldht.kad.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lbms.plugins.mldht.kad.BloomFilterBEP33;
import lbms.plugins.mldht.kad.DBItem;
import lbms.plugins.mldht.kad.DHT;
import lbms.plugins.mldht.kad.DHT.DHTtype;

/**
 * @author Damokles
 *
 */
public class GetPeersResponse extends AbstractLookupResponse {


	private byte[]			scrapeSeeds;
	private byte[]			scrapePeers;

	private List<DBItem>	items;

	/**
	 * @param mtid
	 * @param id
	 * @param nodes
	 * @param token
	 */
	public GetPeersResponse (byte[] mtid, byte[] nodes, byte[] nodes6) {
		super(mtid, Method.GET_PEERS, Type.RSP_MSG);
		this.nodes = nodes;
		this.nodes6 = nodes6;
	}
	
	
	/* (non-Javadoc)
	 * @see lbms.plugins.mldht.kad.messages.MessageBase#apply(lbms.plugins.mldht.kad.DHT)
	 */
	@Override
	public void apply (DHT dh_table) {
		dh_table.response(this);
	}
	
	@Override
	public Map<String, Object> getInnerMap() {
		Map<String, Object> innerMap = super.getInnerMap();
		if(items != null && !items.isEmpty()) {
			List<byte[]> itemsList = new ArrayList<byte[]>(items.size());
			for (DBItem item : items) {
				itemsList.add(item.getData());
			}
			innerMap.put("values", itemsList);
		}

		if(scrapePeers != null && scrapeSeeds != null)
		{
			innerMap.put("BFpe", scrapePeers);
			innerMap.put("BFse", scrapeSeeds);
		}

		return innerMap;
	}

	@Override
	public byte[] getNodes(DHTtype type)
	{
		if(type == DHTtype.IPV4_DHT)
			return nodes;
		if(type == DHTtype.IPV6_DHT)
			return nodes6;
		return null;
	}
	
	public void setPeerItems(List<DBItem> items) {
		this.items = items;
	}

	public List<DBItem> getPeerItems () {
		return items == null ? (List<DBItem>)Collections.EMPTY_LIST : Collections.unmodifiableList(items);
	}
	
	public BloomFilterBEP33 getScrapeSeeds() {
		if(scrapeSeeds != null)
			return new BloomFilterBEP33(scrapeSeeds);
		return null;
	}

	public void setScrapeSeeds(byte[] scrapeSeeds) {
		this.scrapeSeeds = scrapeSeeds;
	}
	
	public void setScrapeSeeds(BloomFilterBEP33 scrapeSeeds) {
		this.scrapeSeeds = scrapeSeeds != null ? scrapeSeeds.serialize() : null;
	}


	public BloomFilterBEP33 getScrapePeers() {
		if(scrapePeers != null)
			return new BloomFilterBEP33(scrapePeers);
		return null;
	}

	public void setScrapePeers(byte[] scrapePeers) {
		this.scrapePeers = scrapePeers;
	}

	public void setScrapePeers(BloomFilterBEP33 scrapePeers) {
		this.scrapePeers = scrapePeers != null ? scrapePeers.serialize() : null;
	}

	@Override
	public String toString() {
		return super.toString() +
			(nodes != null ? (nodes.length/DHTtype.IPV4_DHT.NODES_ENTRY_LENGTH)+" nodes | " : "") +
			(nodes6 != null ? (nodes6.length/DHTtype.IPV6_DHT.NODES_ENTRY_LENGTH)+" nodes6 | " : "") +
			(items != null ? (items.size())+" values | " : "") +
			(scrapePeers != null ? "peer bloom filter | " : "") +
			(scrapeSeeds != null ? "seed bloom filter | " :  "" );
	}
}
