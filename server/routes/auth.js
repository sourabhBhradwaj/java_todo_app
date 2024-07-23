const express = require('express');
const authRouter = express.Router();
const bcryptjs = require("bcryptjs");
const jwt = require("jsonwebtoken");
const User = require("../models/user");
const tokenVerify = require("../middleware/auth");

authRouter.post("/register", async (req, res, next) => {

    console.log(req.body);
    const { username, email, password } = req.body;
    try {
        let user_exist = await User.findOne({ email: email });

        if (user_exist) {
            return res.status(400).json({
                success: false,
                msg: "User Already exists"
            });
        }
        let size = 200;
        const salt = await bcryptjs.genSalt(10);
        const hashedPassword = await bcryptjs.hash(password, salt);
        let user = new User(
            {
                username,
                email,
                password: hashedPassword,
                avatar: "https://gravatar.com/avatar/?s=" + size + "&d=retro"
            }
        );
        const token = jwt.sign({ id: user.id }, "passwordKey"
            // ,{
            //     expiresIn: 36000
            // }, (err, token) => {
            //     if (err) throw err;
            // }
        );
        user = await user.save();
        res.status(200).json({
            success: true,
            msg: "User Registered",
            data: { token, ...user._doc }
        });

    } catch (e) {
        console.log(e);
        res.status(500).json({ error: e.message });
        next();

    }
});


authRouter.post('/login', async (req, res, next) => {

    const email = req.body.email;
    const password = req.body.password;

    try {
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({
                success: false,
                msg: "User with this email does not exists" });
        }
        const isMatch = await bcryptjs.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({
                success:false,
                msg: "Incorrect Password" });
        }
        const token =  jwt.sign({ id: user.id }, "passwordKey");
        res.json(
            {
                success: true,
                msg: "User Login",
                data: { token, ...user._doc }
            });
    } catch (e) {
        console.log(e);
        res.status(500).json({ error: e.message });
        next();
    }


});


authRouter.get("/user", tokenVerify, async (req, res, next) => {

    try {
        const user = await User.findById(req.user).select("-password");
        if (user == null) return res.status(400).json({
            success: true,
            error: "User Not found",

        })
        res.status(200).json({
            success: true,
            msg: "User found",
            data: { token: req.token, ...user._doc }

        })
    } catch (e) {
        console.log(e);
        res.status(500).json({ error: e.message });
        next();
    }

})

module.exports = authRouter;

