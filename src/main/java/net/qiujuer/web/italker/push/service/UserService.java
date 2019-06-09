package net.qiujuer.web.italker.push.service;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.ResponseModel;
import net.qiujuer.web.italker.push.bean.api.user.UpdateInfoModel;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.User;
import net.qiujuer.web.italker.push.factory.PushFactory;
import net.qiujuer.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/user")
public class UserService extends BaseService {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        UserCard card = new UserCard(self, true);
        return ResponseModel.buildOk(card);
    }

    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();

        List<User> users = UserFactory.contacts(self);

        List<UserCard> userCards = users.stream()
                .map(user -> new UserCard(user, true))
                .collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }


    @PUT
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();

        if (self.getId().equalsIgnoreCase(followId)
                || Strings.isNullOrEmpty(followId)) {
            return ResponseModel.buildParameterError();
        }


        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }

        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {

            return ResponseModel.buildServiceError();
        }


        PushFactory.pushFollow(followUser, new UserCard(self));


        return ResponseModel.buildOk(new UserCard(followUser, true));
    }



    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {

            return ResponseModel.buildParameterError();
        }


        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {

            return ResponseModel.buildOk(new UserCard(self, true));
        }


        User user = UserFactory.findById(id);
        if (user == null) {

            return ResponseModel.buildNotFoundUserError(null);
        }


        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }


    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();


        List<User> searchUsers = UserFactory.search(name);

        final List<User> contacts = UserFactory.contacts(self);

        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            || contacts.stream().anyMatch(
                            contactUser -> contactUser.getId()
                                    .equalsIgnoreCase(user.getId())
                    );

                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

}
