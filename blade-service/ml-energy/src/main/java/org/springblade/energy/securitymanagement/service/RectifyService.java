package org.springblade.energy.securitymanagement.service;

import org.springblade.core.mp.base.BaseService;
import org.springblade.core.tool.api.R;
import org.springblade.energy.securitymanagement.entity.Rectify;

import java.util.List;

public interface RectifyService extends BaseService<Rectify> {

	R<Rectify> addRectify(Rectify rectify);

	R<Boolean> removeRectify(List<Rectify> rectify);
}
