package com.derby.nuke.wheatgrass;

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import com.derby.nuke.wheatgrass.entity.Sex
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.service.BirthdayService
import com.derby.nuke.wheatgrass.wechat.service.UserDownloadService
import com.derby.nuke.wheatgrass.wechat.service.WechatService

@Component
@Configurable
@EnableScheduling
class ScheduleConfiguration implements UserDownloadService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	WechatService wechatService;
	@Autowired
	BirthdayService birthdayService;

	@Scheduled(cron = '${download.users.cron}')
	@Override
	void downloadUsers(){
		def userList = wechatService.getUsersByDepartment("1");
		userList.each {item->
			downloadUser(item.userid);
		}
	}
	
	@Scheduled(cron = '${birthday.reminder.cron}')
	void birthdayReminders(){
		birthdayService.sendReminder(LocalDate.now());
	}

	@Override
	void downloadUser(userId){
		def userInfo = wechatService.getUserInfo(userId);
		def user = userRepository.getByUserId(userId);
		if(user == null){
			user = new User();
		}
		user.userId = userInfo.userid;
		user.name = userInfo.name;
		user.email = userInfo.email;
		user.nickName = userInfo.weixinid;
		user.position = userInfo.position;
		if(userInfo.gender == "1"){
			user.sex = Sex.Male;
		} else if(userInfo.gender == "2"){
			user.sex = Sex.Female;
		}
		user.imageUrl = userInfo.avatar;
		userInfo.extattr.attrs.each{attr->
			if(attr.name == "生日" && attr.value != null && attr.value != ""){
				user.birthday = LocalDate.parse(attr.value);
			}else if(attr.name == "籍贯"){
				user.birthplace = attr.value;
			}
		}
		if(userInfo.department!=null && userInfo.department.size() > 0){
			def department = wechatService.getDepartment(userInfo.department[0]);
			user.department = department.name;
		}

		userRepository.saveAndFlush(user);
	}
}
