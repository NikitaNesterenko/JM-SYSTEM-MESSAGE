package jm.controller.rest;


import jm.ChannelService;
import jm.dao.ChannelDAOImpl;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/api/channels/")
public class ChannelTopicRestController {

    private ChannelService channelService;
    private ChannelDAOImpl channelDAO;

    @Autowired
    public void setChannelService(ChannelService channelService, ChannelDAOImpl channelDAO) {
        this.channelService = channelService;
        this.channelDAO = channelDAO;
    }

    @GetMapping("/{id}/topic")
    public ResponseEntity<String> getChannelTopic(@PathVariable Long id) {
        channelDAO.deleteById(id);
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }

    @PutMapping("/{id}/topic/update")
    public ResponseEntity<String> setChannelTopic(@PathVariable Long id, @RequestBody String topic) {
        Channel channel = channelService.getChannelById(id);
        channel.setTopic(topic);
        return ResponseEntity.ok(channelService.getChannelById(id).getTopic());
    }
}
