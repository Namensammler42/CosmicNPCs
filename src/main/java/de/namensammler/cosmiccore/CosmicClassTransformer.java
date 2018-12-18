/*
    CosmicNPCs - A Minecraft mod that allows you to create your own NPCs
    Copyright (C) 2018 Namensammler42

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.namensammler.cosmiccore;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CosmicClassTransformer implements IClassTransformer {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {

		/* Release Obf class */
		if (arg0.equals("aji")) {
			logger.info("** COSMIC-CORE - Injecting new event trigger into 'Block' Class : "
					+ arg0);
			return patchClassASM(arg0, arg2);
		}

		/* Debug class */
		if (arg0.equals("net.minecraft.block.Block")) {
			logger.info("** COSMIC-CORE - Injecting new event trigger into 'Block' Class : "
					+ arg0);
			return patchClassASM(arg0, arg2);
		}
		return arg2;
	}

	public byte[] patchClassASM(String name, byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		Iterator<MethodNode> methods = classNode.methods.iterator();

		
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			
			if ((m.name.equals("onBlockPlacedBy"))
					|| (m.name.equals("a") && m.desc
							.equals("(Lahb;IIILsv;Ladd;)V"))) {
				logger.info("** COSMIC-CORE - Patching onBlockPlacedBy: " + m.name);
				AbstractInsnNode currentNode = null;
				Iterator<AbstractInsnNode> iter = m.instructions.iterator();
				int index = -1;

				while (iter.hasNext()) {
					index++;
					currentNode = iter.next();

					/**
					 * Just prior to the original empty function return, inject code to trigger
					 * our custom block place event.
					 */
					if (currentNode.getOpcode() == RETURN) {
						InsnList toInject = new InsnList();
						toInject.add(new TypeInsnNode(NEW,
								"de/namensammler/cosmiccore/LivingPlaceBlockEvent"));
						toInject.add(new InsnNode(DUP));
						toInject.add(new VarInsnNode(ALOAD, 5));
						toInject.add(new VarInsnNode(ALOAD, 6));
						toInject.add(new VarInsnNode(ILOAD, 2));
						toInject.add(new VarInsnNode(ILOAD, 3));
						toInject.add(new VarInsnNode(ILOAD, 4));
						toInject.add(new MethodInsnNode(INVOKESPECIAL,
								"de/namensammler/cosmiccore/LivingPlaceBlockEvent",
								"<init>",
								"(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;III)V"));
						toInject.add(new VarInsnNode(ASTORE, 7));
						toInject.add(new FieldInsnNode(GETSTATIC,
								"net/minecraftforge/common/MinecraftForge",
								"EVENT_BUS",
								"Lcpw/mods/fml/common/eventhandler/EventBus;"));
						toInject.add(new VarInsnNode(ALOAD, 7));
						toInject.add(new MethodInsnNode(INVOKEVIRTUAL,
								"cpw/mods/fml/common/eventhandler/EventBus",
								"post",
								"(Lcpw/mods/fml/common/eventhandler/Event;)Z"));
						toInject.add(new InsnNode(POP));

						m.instructions.insertBefore(currentNode, toInject);
}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS
				| ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}