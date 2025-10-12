declare namespace API {
  type App = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
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
    appId?: number
  }

  type AppEditRequest = {
    id?: number
    appName?: string
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppUpdateRequest = {
    id?: number
    appName?: string
    cover?: string
    priority?: number
  }

  type AppVO = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
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
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appId?: number
    userId?: number
    messageType?: string
  }

  type chatToGenCodeParams = {
    userMessage: string
    appId: number
  }

  type DeleteRequest = {
    id?: number
  }

  type downloadAppCodeParams = {
    appId: number
  }

  type getAppByIdParams = {
    id: number
  }

  type getAppVOByIdParams = {
    id: number
  }

  type getInfoParams = {
    id: number
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type initChatParams = {
    appId: number
  }

  type LoginUserVO = {
    id?: number
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
    id: number
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    deployKey: string
  }

  type User = {
    id?: number
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
    id?: number
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
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
