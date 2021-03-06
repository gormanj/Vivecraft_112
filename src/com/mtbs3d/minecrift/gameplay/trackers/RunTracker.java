package com.mtbs3d.minecrift.gameplay.trackers;

import com.mtbs3d.minecrift.provider.MCOpenVR;
import com.mtbs3d.minecrift.settings.VRSettings;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RunTracker {


	public boolean isActive(EntityPlayerSP p){
		if(!Minecraft.getMinecraft().vrPlayer.getFreeMove() || Minecraft.getMinecraft().vrSettings.seated)
			return false;
		if(Minecraft.getMinecraft().vrSettings.vrFreeMoveMode != VRSettings.FREEMOVE_RUNINPLACE)
			return false;
		if(p==null || p.isDead)
			return false;
		if(!p.onGround && (p.isInWater() || p.isInLava())) 
			return false;
		if(p.isOnLadder()) 
			return false;
		if(Minecraft.getMinecraft().bowTracker.isNotched())
			return false;
		return true;
	}

	private double direction = 0;
	private double speed = 0;
	private Vec3d movedir;
	
	public double getYaw(){
		return direction;
	}
	
	public double getSpeed(){
		return speed;
	}
	public void doProcess(Minecraft minecraft, EntityPlayerSP player){
		if(!isActive(player)) {
			speed = 0;
			return;
		}

		Vec3d controllerR= minecraft.vrPlayer.vrdata_world_pre.getController(0).getPosition();
		Vec3d controllerL= minecraft.vrPlayer.vrdata_world_pre.getController(1).getPosition();
		
		//Vec3d middle= controllerL.subtract(controllerR).scale(0.5).add(controllerR);

		double c0move = MCOpenVR.controllerHistory[0].averageSpeed(.33);
		double c1move = MCOpenVR.controllerHistory[1].averageSpeed(.33);

		if(speed > 0) {
			if(c0move < 0.1 && c1move < 0.1){
				speed = 0;
				return;
			}
		}else {
			if(c0move < 0.6 && c1move < 0.6){
				speed = 0;
				return;
			}
		}
		
		if(Math.abs(c0move - c1move) > .5){
			speed = 0;
			return;
		} //2 hands plz.
			
		
//		Vec3d hmdPos = minecraft.vrPlayer.getHMDPos_World();
//
//		Vec3d Hmddir = minecraft.vrPlayer.getHMDDir_World();
//		Hmddir= new Vec3d(Hmddir.x,0, Hmddir.z).normalize();	
//		
//		if(speed < 0) Hmddir = movedir; //maybe?
//		
//		Vec3d r = MCOpenVR.controllerHistory[0].netMovement(0.33).rotateYaw(minecraft.vrPlayer.worldRotationRadians);
//		Vec3d l = MCOpenVR.controllerHistory[1].netMovement(0.33).rotateYaw(minecraft.vrPlayer.worldRotationRadians);
//
//		r = new Vec3d(r.x,0, r.z).normalize();		
//		l = new Vec3d(l.x,0, l.z).normalize();	
//		
//		double dotr = r.dotProduct(Hmddir);
//		double dotl = l.dotProduct(Hmddir);
//		
//		if(dotr <0) r = r.scale(-1);
//		if(dotl <0) l = l.scale(-1);
//		
//	//	if(Math.abs(dotr) < 0.5 || Math.abs(dotl) < 0.5) return;
//		
//		movedir = new Vec3d((r.x + l.x) / 2, 0, (r.z + l.z) / 2);
//		
//		Vec3d movedir= new  Vec3d(middle.x - hmdPos.x, 0, middle.z - hmdPos.z);
//		//TODO: do this betterer. Use actual controller movement dir in x-z plane.
//		movedir = movedir.normalize();
		
		//todo: skip entries? is this computationally expensive?
//		Vec3d r = MCOpenVR.controllerHistory[0].averagePosition(.25);
//		Vec3d l = MCOpenVR.controllerHistory[1].averagePosition(.25);
//		
//		Vec3d diff = l.subtract(r).rotateYaw(minecraft.getMinecraft().vrPlayer.worldRotationRadians);
//		
		//double ltor = Math.toDegrees(Math.atan2(-diff.x, diff.z));   
		
		Vec3d v = (minecraft.vrPlayer.vrdata_world_pre.getController(0).getDirection().add(minecraft.vrPlayer.vrdata_world_pre.getController(1).getDirection())).scale(0.5f);
		direction =  (float)Math.toDegrees(Math.atan2(-v.x, v.z)); 
		double spd = (c0move + c1move) / 2;	
		this.speed = spd * 1 * 1.3;
		if(this.speed > .1) this.speed = 1.0f;
		if(this.speed > 1.0) this.speed = 1.3f;
	
	}

}
