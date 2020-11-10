package org.springblade.system.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;
import org.springblade.core.mp.support.Query;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.UserShift;
import org.springblade.system.user.vo.UserJoinUserShiftVo;

public interface UserShiftService extends BaseService<UserShift> {

	R<IPage<UserJoinUserShiftVo>> userShiftList(Query query, String time, Long stationId, Long uid);
}
