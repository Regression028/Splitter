class ApiResponse {
    static success(res, data = {}, statusCode = 200) {
        return res.status(statusCode).json({
            success: true,
            data
        });
    }

    static error(res, statusCode = 500, message = 'An unexpected error occurred', code = 'INTERNAL_ERROR') {
        return res.status(statusCode).json({
            success: false,
            error: {
                code,
                message
            }
        });
    }
}

module.exports = ApiResponse;
