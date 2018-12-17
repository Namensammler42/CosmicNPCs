package de.namensammler.cosmicnpcs.npcsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPCRecorder {
	public RecordThread recordThread;
	public List<NPCAction> eventsList = Collections
			.synchronizedList(new ArrayList<NPCAction>());
	public String fileName;
}