package panda.birdsnests;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;


public class OnJoinWorldHandler {
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(PlayerTickEvent event)
    {
      
        if (!BirdsNests.haveWarnedVersionOutOfDate && event.player.worldObj.isRemote 
              && !BirdsNests.versionChecker.isLatestVersion())
        {
            ClickEvent versionCheckChatClickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, 
                  "https://minecraft.curseforge.com/projects/birds-nests");
            ChatStyle clickableChatStyle = new ChatStyle().setChatClickEvent(versionCheckChatClickEvent);
            ChatComponentText versionWarningChatComponent = 
                  new ChatComponentText("Your Bird's Nests Mod is not latest version!  Click this message to update.");
            versionWarningChatComponent.setChatStyle(clickableChatStyle);
            event.player.addChatMessage(versionWarningChatComponent);
            BirdsNests.haveWarnedVersionOutOfDate = true;
        }
      
    }
}