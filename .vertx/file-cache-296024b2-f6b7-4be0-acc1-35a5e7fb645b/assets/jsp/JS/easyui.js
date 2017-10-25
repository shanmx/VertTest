// easyui文件
		$.messager.defaults = {
		ok: "是", cancel: "否",
		draggable:true,
		modal:true,
		width:'250px',
		height:'150px',
		overflow:'none',
		left: '30%',
		right: '30%',
	};

     
var url;  
$.fn.validatebox.defaults.missingMessage = '该输入项为必输项';
function newRole(){
	$('#dlgRoleInfo').dialog('open').dialog('setTitle','新建角色');
	$('#fmRoleInfo').form('clear');
	url = 'insertRole';
}
function editRole(){
	var row = $('#datagrid').datagrid('getSelected');
	if (row){
		$('#dlgRoleInfo').dialog('open').dialog('setTitle','编辑角色');
		$('#fmRoleInfo').form('load',row);
		url = '/role/updateRole';//+row.id;
	}
}
	
function saveRole(){
	$('#fmRoleInfo').form('submit',{
		url: url,
		onSubmit: function(){
			return $(this).form('validate');
		},
		success: function(result){
			var result = eval('('+result+')');
			if (result.errorMsg){
				$.messager.show({
					title: '错误提示',
					msg: result.errorMsg
				});
			} else {
				$('#dlgRoleInfo').dialog('close');		// close the dialog
				$('#datagrid').datagrid('reload');	// reload the Role data
			}
		}
	});
}
function destroyRole(){
	var row = $('#datagrid').datagrid('getSelected');
	if (row){
		$.messager.confirm('确认','您确认删除这个角色吗?',function(r){
			if (r){
				$.post('deleteRole',{id:row.id},function(result){
					if (result.success){
						$('#datagrid').datagrid('reload');	// reload the Role data
					} else {
						$.messager.show({	// show error message
							title: '错误提示',
							msg: result.errorMsg
						});
					}
				},'json');
			}
		});
	}
}