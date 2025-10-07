declare namespace API {
  type App = {
    id?: number | string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number | string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type AppAddRequest = {
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
  }

  type AppDeployRequest = {
    appId?: number | string
  }

  type AppEditRequest = {
    id?: number | string
    appName?: string
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number | string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number | string
  }

  type AppUpdateRequest = {
    id?: number | string
    appName?: string
    cover?: string
    priority?: number
  }

  type AppVO = {
    id?: number | string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number | string
    createTime?: string
  }

  type BaseResponseApp = {
    code?: number
    data?: App
    msg?: string
  }

  type BaseResponseAppVO = {
    code?: number
    data?: AppVO
    msg?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    msg?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    msg?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    msg?: string
  }

  type BaseResponseMapStringObject = {
    code?: number
    data?: Record<string, any>
    msg?: string
  }

  type BaseResponsePageAppVO = {
    code?: number
    data?: PageAppVO
    msg?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    msg?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    msg?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    msg?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    msg?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    msg?: string
  }

  type ChatHistory = {
    id?: number | string
    message?: string
    messageType?: string
    appId?: number | string
    userId?: number | string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number | string
    appId?: number | string
    userId?: number | string
    messageType?: string
  }

  type chatToGenCodeParams = {
    userMessage: string
    appId: number | string
  }

  type DeleteRequest = {
    id?: number | string
  }

  type getAppByIdParams = {
    id: number | string
  }

  type getAppVOByIdParams = {
    id: number | string
  }

  type getInfoParams = {
    id: number | string
  }

  type getUserByIdParams = {
    id: number | string
  }

  type getUserVOByIdParams = {
    id: number | string
  }

  type initChatParams = {
    appId: number | string
  }

  type LoginUserVO = {
    id?: number | string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type pageParams = {
    page: PageChatHistory
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type removeParams = {
    id: number | string
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    deployKey: string
  }

  type User = {
    id?: number | string
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserAddRequest = {
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number | string
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateRequest = {
    id?: number | string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number | string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
